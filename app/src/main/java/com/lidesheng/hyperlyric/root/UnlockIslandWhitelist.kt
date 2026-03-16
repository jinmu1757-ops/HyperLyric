package com.lidesheng.hyperlyric.root

import android.annotation.SuppressLint
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import java.lang.reflect.Method
import kotlin.math.max

/**
 * 灵动岛小窗白名单：通过原生 Xposed 的 handleInitPackageResources 替换资源。
 * 仅在 HyperOS 3.0.300 及以上版本生效
 *
 * 注：此功能保留使用原生 Xposed API。
 */
object UnlockIslandWhitelist {
    private const val TARGET_PACKAGE = "miui.systemui.plugin"
    private const val TARGET_RES_NAME = "config_dynamic_island_miniwindow_media_whitelist"
    private const val TARGET_RES_TYPE = "array"
    private const val MIN_VERSION = "3.0.300"

    private val VERSION_SPLIT_REGEX = Regex("[^0-9]+")

    private val getSystemPropMethod: Method? by lazy {
        try {
            @SuppressLint("PrivateApi")
            val clazz = Class.forName("android.os.SystemProperties")
            clazz.getMethod("get", String::class.java, String::class.java)
        } catch (_: Exception) {
            null
        }
    }

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
        "cn.kuwo.player",               // 酷我音乐
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
     * 原生 Xposed 资源 Hook 入口，由 HookEntry.handleInitPackageResources() 调用
     */
    fun init(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        if (resparam.packageName != TARGET_PACKAGE) return

        if (!isVersionCompatible()) return

        try {
            val res = resparam.res
            @Suppress("DiscouragedApi")
            val resId = res.getIdentifier(TARGET_RES_NAME, TARGET_RES_TYPE, TARGET_PACKAGE)

            if (resId != 0) {
                res.setReplacement(resId, CUSTOM_WHITELIST)
                XposedBridge.log("[HyperLyric] 成功替换灵动岛白名单，生效数量: ${CUSTOM_WHITELIST.size}")
            } else {
                XposedBridge.log("[HyperLyric] 未找到白名单资源 $TARGET_RES_NAME")
            }
        } catch (t: Throwable) {
            XposedBridge.log("[HyperLyric] Hook资源时发生错误: ${t.message}")
        }
    }

    private fun isVersionCompatible(): Boolean {
        try {
            val rawVersion = getSystemPropMethod?.invoke(null, "ro.build.version.incremental", "unknown") as? String ?: "unknown"

            if (rawVersion.isEmpty() || rawVersion == "unknown") {
                XposedBridge.log("[HyperLyric] 获取系统版本失败，白名单替换跳过。")
                return false
            }

            val cleanVersion = rawVersion.dropWhile { !it.isDigit() }
            val v1 = cleanVersion.split(VERSION_SPLIT_REGEX).filter { it.isNotEmpty() }
            val v2 = MIN_VERSION.split(VERSION_SPLIT_REGEX).filter { it.isNotEmpty() }
            val length = max(v1.size, v2.size)

            for (i in 0 until length) {
                val a = v1.getOrNull(i)?.toIntOrNull() ?: 0
                val b = v2.getOrNull(i)?.toIntOrNull() ?: 0
                if (a != b) return a > b
            }
            return true
        } catch (e: Exception) {
            XposedBridge.log("[HyperLyric] 版本检测异常: ${e.message}")
            return false
        }
    }
}
