package com.lidesheng.hyperlyric.root

import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam

class HookEntry : XposedModule() {

    override fun onPackageLoaded(param: PackageLoadedParam) {
        val packageName = param.packageName
        if (packageName == "com.android.systemui" || packageName == "miui.systemui.plugin") {
            log("[HyperLyric] ★ onPackageLoaded: $packageName")
            //MainHook.hookSystemUIDynamicIsland(this, param)
            UnlockIslandWhitelist.hook(this)
        }
    }
}
