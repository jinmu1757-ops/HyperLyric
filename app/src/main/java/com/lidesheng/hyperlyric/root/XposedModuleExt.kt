package com.lidesheng.hyperlyric.root

import android.util.Log
import io.github.libxposed.api.XposedModule

/**
 * XposedModule 的扩展函数，用于简化日志打印。
 * 将原生的 log(priority, tag, msg) 桥接为简单的 log(msg)。
 */
internal fun XposedModule.log(msg: String) {
    // 统一使用 HyperLyric 作为 Tag，Log.INFO 作为默认优先级
    this.log(Log.INFO, "HyperLyric", msg)
}
