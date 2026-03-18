package com.lidesheng.hyperlyric.root

import android.annotation.SuppressLint
import android.content.res.Resources
import io.github.libxposed.api.XposedInterface.Chain
import io.github.libxposed.api.XposedInterface.Hooker
import io.github.libxposed.api.XposedModule

/**
 * 灵动岛小窗白名单：通过方法 Hook 拦截 Resources.getStringArray() 替换白名单。
 * 只有当前系统中存在名为 config_dynamic_island_miniwindow_media_whitelist 的资源数组，就会自动启动 Hook。
 */
object UnlockIslandWhitelist {
    private const val TARGET_RES_NAME = "config_dynamic_island_miniwindow_media_whitelist"

    // 缓存目标资源 ID
    private var targetResId: Int = -1
    // 用于记录已经通过反射查询过但并不是目标 ID 的集合，避免高频抛出异常导致严重掉帧
    private val checkedIds = mutableSetOf<Int>()

    // 保存 XposedModule 引用
    internal lateinit var module: XposedModule

    // 自定义白名单列表
    private val CUSTOM_WHITELIST = arrayOf(
        "tv.danmaku.bili",              // 哔哩哔哩
        "tv.danmaku.bilibilihd",        // 哔哩哔哩HD
        "com.google.android.youtube",   // YouTube
        "com.ss.android.ugc.aweme",     // 抖音
        "com.ss.android.ugc.aweme.mobile",
        "com.tencent.qqmusic",          // QQ音乐
        "com.tencent.qqmusicpad",       // QQ音乐Pad
        "com.kugou.android",            // 酷狗音乐
        "com.kugou.android.lite",       // 酷狗音乐概念版
        "cn.kuwo.player",              // 酷我音乐
        "com.netease.cloudmusic",       // 网易云音乐
        "cmccwm.mobilemusic",           // 咪咕音乐
        "cn.wenyu.bodian",              // 波点音乐
        "com.luna.music",               // 汽水音乐
        "com.ikunshare.music.mobile",
        "com.miui.player",              // 小米音乐
        "com.apple.android.music",      // Apple Music
        "com.google.android.apps.youtube.music", // YT Music
        "com.spotify.music",            // Spotify
        "com.salt.music",               // Salt Player
        "com.xuncorp.qinalt.music",     // 青盐云听
        "com.maxmpz.audioplayer",       // Poweramp
        "yos.music.player",             // Flamingo
        "com.larus.nova",               // 汽水音乐
        "com.sumsg.musichub",           // Music Hub
        "com.miui.fm",                  // 小米收音机
        "com.xs.fm",                    // 喜马拉雅
        "com.xs.fm.lite",               // 喜马拉雅极速版
        "com.ximalaya.ting.android",    // 喜马拉雅
        "com.tencent.weread",           // 微信读书
        "com.dragon.read",              // 番茄小说
        "com.qidian.QDReader",          // 起点读书
        "com.tencent.mm",               // 微信
        "org.telegram.messenger",       // Telegram
        "com.sina.weibo",               // 微博
        "cn.toside.music.mobile"
    )

    /**
     * 方法 Hook 入口，由 HookEntry.onPackageLoaded() 调用
     */
    @SuppressLint("DiscouragedApi")
    fun hook(xposedModule: XposedModule) {
        module = xposedModule

        // 功能探测：首先尝试在系统资源集中查找目标白名单 ID
        val resId = try {
            Resources.getSystem().getIdentifier(TARGET_RES_NAME, "array", "android")
        } catch (_: Exception) {
            0
        }

        if (resId != 0) {
            targetResId = resId
            module.log("[HyperLyric] 功能探测：系统已暴露白名单资源 ID: $resId")
        } else {
            // 如果系统直接查不到，我们仍启动 Hook，由拦截器尝试在 Package 运行时进行二次动态探测
            module.log("[HyperLyric] 功能探测：系统底层未直接暴露 ID，进入运行时动态匹配模式。")
        }

        try {
            val getStringArrayMethod = Resources::class.java.getDeclaredMethod(
                "getStringArray", Int::class.javaPrimitiveType
            )
            module.deoptimize(getStringArrayMethod)
            module.hook(getStringArrayMethod).intercept(GetStringArrayHooker())
            module.log("[HyperLyric] 白名单方法 Hook 已注册 (探测模式)")
        } catch (e: Exception) {
            module.log("[HyperLyric] [E] 白名单 Hook 注册失败: ${e.message}")
        }
    }

    /**
     * Hooker: 拦截 Resources.getStringArray(int)，替换白名单资源
     * 极速判断策略：缓存已知目标 ID，无效 ID 仅检查一次。
     */
    class GetStringArrayHooker : Hooker {
        override fun intercept(chain: Chain): Any? {
            val resId = chain.args[0] as? Int ?: return chain.proceed()

            // 1. 已知目标 ID 情况下的极速相等判断
            if (targetResId != -1) {
                if (resId == targetResId) {
                    return CUSTOM_WHITELIST
                }
                return chain.proceed()
            }

            // 2. 动态探测流程：记录已检查过的 ID 防止重复高频报错
            if (!checkedIds.add(resId)) {
                return chain.proceed()
            }

            // 3. 全新的未检查过的 resId，此时通过名字反射进行确认
            try {
                val resources = chain.thisObject as? Resources ?: return chain.proceed()
                val resName = resources.getResourceEntryName(resId)
                if (resName == TARGET_RES_NAME) {
                    targetResId = resId
                    module.log("[HyperLyric] 运行时探测成功，锁定白名单 ID: $resId")
                    return CUSTOM_WHITELIST
                }
            } catch (_: Exception) {
                // 静默处理无关资源
            }
            return chain.proceed()
        }
    }
}
