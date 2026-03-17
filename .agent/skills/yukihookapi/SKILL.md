---
name: yukihookapi
description: YukiHookAPI 的使用规范、常见 DSL 语法和重构指南，用于开发使用 Kotlin 编写的 Xposed 模块。
---

# YukiHookAPI 指南

本技能用于指导在 Android 项目中使用 YukiHookAPI 开发 Xposed 模块，替换繁琐的原生 Java 写法。

## 1. 基础配置与依赖引入

### build.gradle.kts 配置
需要配置 KSP 插件（用于生成目标入口）与 YukiHookAPI 依赖。

```kotlin
plugins {
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" // 请根据 Kotlin 版本匹配 KSP
}

dependencies {
    implementation("com.highcapable.yukihookapi:api:1.2.1") // 注意: 1.2+ 或 1.3+ 版本
    compileOnly("de.robv.android.xposed:api:82") // Xposed API (必须 compileOnly)
    ksp("com.highcapable.yukihookapi:ksp-xposed:1.2.1") // KSP 处理器
}
```

## 2. 模块配置入口

原生的 `xposed_init` 和繁杂的 `IXposedHookLoadPackage`、`IXposedHookInitPackageResources` 不再需要手写。通过注解 `@InjectYukiHookWithXposed` 自动生成。

```kotlin
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import com.highcapable.yukihookapi.YukiHookAPI

@InjectYukiHookWithXposed(
    isUsingResourcesHook = true // 如果需要 Hook 资源文件，必须设置为 true
)
object HookEntry : IYukiHookXposedInit {
    override fun onHook() = YukiHookAPI.encase {
        // App作用域
        loadApp(name = "com.android.systemui") {
            // ... 方法 Hook 代码
        }
        
        loadApp(name = "miui.systemui.plugin") {
            // ... 资源 Hook 代码
        }
    }
}
```
*注：编译后自动生成 `assets/xposed_init`。无需手动维护目标类全名。*

## 3. Hook DSL 语法

### 获取 Class 并 Hook 方法

```kotlin
// 方式1：已存在的类
Activity::class.resolve().firstMethod {
    name = "onCreate"
    parameters(Bundle::class)
}.hook {
    after {
        val activity = instance<Activity>() // 强转 instance 获取对象
        // Your code here.
    }
}

// 方式2：字符串转 Class (包内类)
"com.example.demo.TestClass".toClass().resolve().firstMethod {
    name = "doSomething"
    // emptyParameters() // 如果没有参数
}.hook {
    before {
        // args(0).set(...) 修改参数
    }
    after {
        // result = ... 修改返回值
    }
}
```

### 连续 Hook (使用 apply)
```kotlin
Activity::class.resolve().apply {
    firstMethod { name = "onCreate"; param(Bundle::class) }.hook {
        after { /* ... */ }
    }
    firstMethod { name = "onStart"; emptyParam() }.hook {
        after { /* ... */ }
    }
}
```

### 反射获取 Field / 执行 Method
```kotlin
// 获取变量值
field { name = "HOOKED_FACTORY" }.get(instance).any()

// 设置变量值
field { name = "CLIP_DISABLED" }.get(instance).set(true)

// 执行方法
val extras = method { name = "getExtras" }.get(instance).call()
```

## 4. Resources Hook DSL (替换 config_xxx 数组)

如果 `@InjectYukiHookWithXposed(isUsingResourcesHook = true)` 开启，即可使用：

```kotlin
loadApp(name = "miui.systemui.plugin") {
    resources().hook {
        injectResource {
            conditions {
                name = "config_dynamic_island_miniwindow_media_whitelist"
                array() // 或 type = "array"
            }
            replaceTo(arrayOf("app1", "app2"))
        }
    }
}
```

## 5. 日志与异常处理

在 hook 代码段中，可以直接使用：
- `loggerD(msg = "...")`
- `loggerE(msg = "...", e = exception)`
这样排版比手动 `XposedBridge.log` 更好看，会附带 Tag 和状态。
