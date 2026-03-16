package com.lidesheng.hyperlyric.root

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.Choreographer
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isEmpty
import com.highcapable.yukihookapi.hook.log.YLog
import com.lidesheng.hyperlyric.utils.AnimUtils
import java.io.File
import java.lang.ref.WeakReference
import java.util.WeakHashMap
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max

object MainHook {
        private const val LEFT_VIEW_TAG = "MY_ISLAND_TITLE_LEFT"
        private const val RIGHT_VIEW_TAG = "MY_ISLAND_TITLE_RIGHT"
        private const val LANDSCAPE_MAX_WIDTH_DP = 185f
        private const val DEFAULT_CAMERA_GAP_PX = 68
        private const val FIXED_X_OFFSET_PX = 71f
        private const val RIGHT_EXTRA_OFFSET_PX = 6f

        private var cachedConfig: ModuleConfig? = null
        private var lastFileModifiedTime: Long = 0
        private val configFile = File("/data/system/hyperlyric.conf")
        private val scrollerMap = WeakHashMap<TextView, MarqueeController>()
        private var mediaTracker: MediaSessionTracker? = null
        private var activeIslandState: ActiveIslandState? = null
        private var cachedCutoutWidthPortrait: Int = -2
        private var cachedCutoutWidthLandscape: Int = -2
        private val clipDisabledViews = WeakHashMap<View, Boolean>()
        private val layoutFixedContainers = WeakHashMap<ViewGroup, Boolean>()

        private data class ActiveIslandState(
            val tvLeft: WeakReference<TextView>,
            val tvRight: WeakReference<TextView>,
            val bigIslandView: WeakReference<ViewGroup>,
            val pkgName: String
        ) {
            fun isAlive(): Boolean =
                tvLeft.get() != null && tvRight.get() != null && bigIslandView.get() != null
        }

        data class ModuleConfig(
            val size: Int = 13,
            val marquee: Boolean = true,
            val hideNotch: Boolean = false,
            val maxLeftWidth: Int = 240,
            val speed: Int = 100,
            val delay: Int = 1500,
            val animMode: Int = 0,
            val whitelist: Set<String> = emptySet()
        )

    fun hookSystemUIDynamicIsland() {
            YLog.debug(msg = "[HyperLyric] ★ hookSystemUIDynamicIsland start")

            try {
                de.robv.android.xposed.XposedHelpers.findAndHookMethod(
                    ClassLoader::class.java,
                    "loadClass",
                    String::class.java,
                    object : de.robv.android.xposed.XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            try {
                                val cls = param.result as? Class<*> ?: return
                                if (cls.name != "miui.systemui.dynamicisland.template.IslandTemplateFactory") return

                                val hookedField = try {
                                    cls.classLoader?.javaClass?.getDeclaredField("HOOKED_FACTORY")
                                        ?.apply { isAccessible = true }
                                } catch (_: NoSuchFieldException) {
                                    null
                                }

                                if (hookedField != null && hookedField.get(cls.classLoader) == true) return
                                hookedField?.set(cls.classLoader, true)

                                hookFactoryMethodDynamic(cls)
                            } catch (e: Exception) {
                                YLog.error(msg = "[HyperLyric] loadClass hook 异常: ${e.message}")
                            }
                        }
                    }
                )
            } catch (e: Exception) {
                YLog.error(msg = "[HyperLyric] [E] Module injection failed: ${e.message}")
            }
        }

        private fun getSmartConfig(): ModuleConfig {
            if (!configFile.exists() || !configFile.canRead()) {
                return cachedConfig ?: ModuleConfig().also { cachedConfig = it }
            }

            val currentModified = configFile.lastModified()
            if (cachedConfig != null && currentModified == lastFileModifiedTime) return cachedConfig!!

            return try {
                val content = configFile.readText().trim()
                parseConfig(content).also {
                    cachedConfig = it
                    lastFileModifiedTime = currentModified
                }
            } catch (e: Exception) {
                YLog.error(msg = "[HyperLyric] [E] 配置文件解析失败: ${e.message}")
                cachedConfig ?: ModuleConfig()
            }
        }

        private fun parseConfig(content: String): ModuleConfig {
            val map = content.split(";")
                .mapNotNull { it.split("=", limit = 2).takeIf { kv -> kv.size == 2 } }
                .associate { (k, v) -> k.trim() to v.trim() }

            return ModuleConfig(
                size = map["size"]?.toIntOrNull() ?: 13,
                marquee = map["marquee"] == "true",
                hideNotch = map["hideNotch"] == "true",
                maxLeftWidth = map["maxLeftWidth"]?.toIntOrNull() ?: 240,
                speed = map["speed"]?.toIntOrNull() ?: 100,
                delay = map["delay"]?.toIntOrNull() ?: 1500,
                animMode = map["animMode"]?.toIntOrNull() ?: 0,
                whitelist = map["whitelist"]
                    ?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }?.toSet()
                    ?: emptySet()
            )
        }

        private fun getNaturalCutoutWidth(view: View): Int {
            val landscape = isLandscape(view.context)
            val cached = if (landscape) cachedCutoutWidthLandscape else cachedCutoutWidthPortrait
            if (cached != -2) return cached
            val rects = view.rootWindowInsets?.displayCutout?.boundingRects ?: return -1
            if (rects.isEmpty()) return -1
            val screenCenter = view.resources.displayMetrics.widthPixels / 2
            val result =
                rects.firstOrNull { abs((it.left + it.right) / 2 - screenCenter) < 200 }?.width()
                    ?: rects[0].width()
            if (landscape) cachedCutoutWidthLandscape = result else cachedCutoutWidthPortrait =
                result
            return result
        }

        private fun calcRealGapPx(view: View): Int {
            val detected = getNaturalCutoutWidth(view)
            return if (detected > 0) detected else DEFAULT_CAMERA_GAP_PX
        }

        private fun calcDynamicOffset(view: View): Float =
            FIXED_X_OFFSET_PX + (calcRealGapPx(view) - DEFAULT_CAMERA_GAP_PX).toFloat()

        private fun disableClipOnParents(view: View, rootParent: View?) {
            if (clipDisabledViews[view] == true) return
            var p = view.parent
            while (p is ViewGroup) {
                p.clipChildren = false
                p.clipToPadding = false
                if (p == rootParent) break
                p = p.parent
            }
            clipDisabledViews[view] = true
        }

        private fun onMediaTitleChanged(pkg: String, newTitle: String) {
            try {
                val state = activeIslandState ?: return
                if (state.pkgName != pkg || !state.isAlive()) return

                val tvLeft = state.tvLeft.get() ?: return
                val tvRight = state.tvRight.get() ?: return
                val bigIslandView = state.bigIslandView.get() ?: return
                val config = getSmartConfig()

                if (newTitle.isEmpty() || tvLeft.contentDescription == newTitle) return

                tvLeft.contentDescription = newTitle
                applyLyricContent(tvLeft, tvRight, newTitle, config, tvLeft.context, bigIslandView)
            } catch (e: Exception) {
                YLog.error(msg = "[HyperLyric] onMediaTitleChanged 异常: ${e.message}")
            }
        }

        private fun showLeftOnly(tvLeft: TextView, tvRight: TextView) {
            tvRight.text = ""
            tvRight.visibility = View.GONE
            tvLeft.visibility = View.VISIBLE
            tvLeft.bringToFront()
        }

        private fun hookFactoryMethodDynamic(factoryClass: Class<*>) {
            val methods = factoryClass.declaredMethods
            val targetMethod = methods.firstOrNull { it.name == "createBigIslandTemplateView" }

            if (targetMethod == null) {
                YLog.error(msg = "[HyperLyric] [E] 未找到 createBigIslandTemplateView 方法，可能系统版本不兼容")
                YLog.warn(msg = "[HyperLyric] 当前工厂类可用方法: ${methods.joinToString { it.name }}")
                return
            }

            de.robv.android.xposed.XposedBridge.hookMethod(
                targetMethod,
                object : de.robv.android.xposed.XC_MethodHook() {
                    @SuppressLint("DiscouragedApi")
                    override fun afterHookedMethod(param: MethodHookParam) {
                        try {
                            if (param.args.getOrNull(0) == null) return
                            if (param.args.getOrNull(4) == true) return
                            val bigIslandView = param.result as? ViewGroup ?: return
                            val context = bigIslandView.context
                            val config = getSmartConfig()

                            if (mediaTracker == null) {
                                mediaTracker = MediaSessionTracker(context).apply {
                                    onTitleChanged =
                                        { pkg, title -> onMediaTitleChanged(pkg, title) }
                                }
                            }

                            val extraInfo = param.args.getOrNull(2)
                            val pkgName = extraInfo?.let { info ->
                                try {
                                    val bundle = info.javaClass.getMethod("getExtras")
                                        .invoke(info) as? Bundle
                                    bundle?.getString("miui.pkg.name")
                                } catch (_: Exception) {
                                    null
                                }
                            }

                            if (pkgName == null) return
                            if (pkgName !in config.whitelist) return

                            val songTitle = mediaTracker?.getSongTitle(pkgName) ?: ""
                            if (songTitle.isEmpty()) return
                            if (bigIslandView.isEmpty()) return

                            val mainRowContainer = bigIslandView.getChildAt(0) as? ViewGroup
                            if (mainRowContainer == null) {
                                YLog.error(msg = "[HyperLyric] [E] bigIslandView 子视图结构异常，无法获取 mainRowContainer")
                                return
                            }
                            fixLinearLayoutForWrapContent(mainRowContainer)

                            if (mainRowContainer.isEmpty()) return
                            val realAlbumContainer = mainRowContainer.getChildAt(0) as? ViewGroup
                            val areaCutout = bigIslandView.getChildAt(1) as? ViewGroup
                            if (realAlbumContainer == null || areaCutout == null) {
                                YLog.error(msg = "[HyperLyric] [E] 视图结构不完整: realAlbumContainer=${realAlbumContainer != null}, areaCutout=${areaCutout != null}")
                                return
                            }

                            val (tvLeft, tvRight, isNewView) = ensureTextViews(
                                context, config, realAlbumContainer, areaCutout
                            )

                            if (isNewView) {
                                setupLayoutListeners(
                                    tvLeft,
                                    tvRight,
                                    bigIslandView,
                                    mainRowContainer,
                                    realAlbumContainer,
                                    areaCutout
                                )
                            }

                            activeIslandState = ActiveIslandState(
                                WeakReference(tvLeft), WeakReference(tvRight),
                                WeakReference(bigIslandView), pkgName
                            )

                            if (tvLeft.contentDescription == songTitle) return
                            tvLeft.contentDescription = songTitle
                            applyLyricContent(
                                tvLeft,
                                tvRight,
                                songTitle,
                                config,
                                context,
                                bigIslandView
                            )
                        } catch (e: Exception) {
                            YLog.error(msg = "[HyperLyric] [E] createBigIslandTemplateView hook 异常: ${e.message}")
                        }
                    }
                }
            )
        }

        private fun fixLinearLayoutForWrapContent(container: ViewGroup) {
            if (container !is LinearLayout) return
            if (layoutFixedContainers[container] == true) return
            if (container.gravity != (Gravity.START or Gravity.CENTER_VERTICAL)) {
                container.gravity = Gravity.START or Gravity.CENTER_VERTICAL
            }
            for (i in 0 until container.childCount) {
                val lp =
                    container.getChildAt(i).layoutParams as? LinearLayout.LayoutParams ?: continue
                if (lp.weight > 0 || lp.width == 0) {
                    lp.weight = 0f
                    lp.width = ViewGroup.LayoutParams.WRAP_CONTENT
                    container.getChildAt(i).layoutParams = lp
                }
            }
            layoutFixedContainers[container] = true
        }

        private data class TextViewPair(val left: TextView, val right: TextView, val isNew: Boolean)

        private fun ensureTextViews(
            context: Context, config: ModuleConfig,
            leftContainer: ViewGroup, rightContainer: ViewGroup
        ): TextViewPair {
            var isNew = false
            val tvLeft = leftContainer.findViewWithTag<TextView>(LEFT_VIEW_TAG)
                ?: createBaseTextView(context, LEFT_VIEW_TAG, config.size).also {
                    leftContainer.addView(it); isNew = true
                }
            val tvRight = rightContainer.findViewWithTag<TextView>(RIGHT_VIEW_TAG)
                ?: createBaseTextView(context, RIGHT_VIEW_TAG, config.size).also {
                    rightContainer.addView(it); isNew = true
                }
            return TextViewPair(tvLeft, tvRight, isNew)
        }

        private fun setupLayoutListeners(
            tvLeft: TextView, tvRight: TextView,
            bigIslandView: ViewGroup, mainRowContainer: ViewGroup,
            realAlbumContainer: ViewGroup, areaCutout: ViewGroup
        ) {
            arrayOf(mainRowContainer, realAlbumContainer, areaCutout).forEach {
                it.elevation = 1000f
                it.translationZ = 1000f
            }

            tvLeft.addOnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
                try {
                    val leftView = v as TextView
                    val rootParent = bigIslandView.parent as? View
                    disableClipOnParents(leftView, rootParent)
                    disableClipOnParents(tvRight, rootParent)

                    if (isLandscape(leftView.context)) {
                        leftView.translationX =
                            (calcRealGapPx(leftView) - DEFAULT_CAMERA_GAP_PX).toFloat()
                    } else {
                        leftView.translationX = calcDynamicOffset(leftView)
                    }
                    leftView.translationY = 0f
                } catch (e: Exception) {
                    YLog.error(msg = "[HyperLyric] tvLeft layout 异常: ${e.message}")
                }
            }

            tvRight.addOnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
                try {
                    v.translationX = calcDynamicOffset(v) + RIGHT_EXTRA_OFFSET_PX
                    v.translationY = 0f
                } catch (e: Exception) {
                    YLog.error(msg = "[HyperLyric] tvRight layout 异常: ${e.message}")
                }
            }
        }

        private fun applyLyricContent(
            tvLeft: TextView, tvRight: TextView,
            songTitle: String, config: ModuleConfig,
            context: Context, bigIslandView: ViewGroup
        ) {
            try {
                stopMarquee(tvLeft)
                stopMarquee(tvRight)

                tvLeft.setTextSize(TypedValue.COMPLEX_UNIT_SP, config.size.toFloat())
                tvRight.setTextSize(TypedValue.COMPLEX_UNIT_SP, config.size.toFloat())

                val paint = tvLeft.paint
                val totalTextWidthPx = paint.measureText(songTitle)
                val realGapPx = calcRealGapPx(tvLeft)
                val dynamicOffset = calcDynamicOffset(tvLeft)

                tvLeft.setPadding(0, 0, 0, 0)
                (tvLeft.layoutParams as? ViewGroup.MarginLayoutParams)?.let {
                    it.rightMargin = 0
                    tvLeft.layoutParams = it
                }
                disableClipOnParents(tvLeft, bigIslandView.parent as View)

                when {
                    config.hideNotch -> applyHideNotchMode(
                        tvLeft,
                        tvRight,
                        songTitle,
                        config,
                        context,
                        totalTextWidthPx,
                        realGapPx,
                        dynamicOffset
                    )

                    isLandscape(context) -> applyLandscapeMode(
                        tvLeft,
                        tvRight,
                        songTitle,
                        config,
                        context,
                        totalTextWidthPx
                    )

                    else -> applyPortraitSplitMode(
                        tvLeft,
                        tvRight,
                        songTitle,
                        config,
                        paint,
                        totalTextWidthPx,
                        realGapPx,
                        dynamicOffset
                    )
                }
            } catch (e: Exception) {
                YLog.error(msg = "[HyperLyric] applyLyricContent 异常: ${e.message}")
            }
        }

        private fun applyHideNotchMode(
            tvLeft: TextView, tvRight: TextView,
            title: String, config: ModuleConfig, context: Context,
            totalWidthPx: Float, realGapPx: Int, offset: Float
        ) {
            showLeftOnly(tvLeft, tvRight)
            val maxLeftWidthPx = config.maxLeftWidth
            val halfOverflow = (totalWidthPx / 2) > (maxLeftWidthPx - realGapPx)

            val containerLength = if (halfOverflow) maxLeftWidthPx
            else ((totalWidthPx.toInt() + realGapPx) / 2 - dp2px(context, 2f))

            val actualViewWidth =
                if (halfOverflow) (containerLength * 2 - realGapPx + dp2px(context, 4f))
                else ceil(totalWidthPx).toInt()

            tvLeft.layoutParams = tvLeft.layoutParams.apply {
                width = actualViewWidth
                (this as? ViewGroup.MarginLayoutParams)?.rightMargin =
                    containerLength - actualViewWidth
            }

            AnimUtils.applyTextWithAnim(tvLeft, title, config.animMode, offset) {
                if (config.marquee) startMarquee(tvLeft, config)
                else tvLeft.ellipsize = TextUtils.TruncateAt.END
            }
        }

        private fun applyLandscapeMode(
            tvLeft: TextView, tvRight: TextView,
            title: String, config: ModuleConfig, context: Context,
            totalWidthPx: Float
        ) {
            showLeftOnly(tvLeft, tvRight)
            val maxLandWidthPx = dp2px(context, LANDSCAPE_MAX_WIDTH_DP)
            val containerWidth =
                if (totalWidthPx < maxLandWidthPx) ceil(totalWidthPx).toInt() else maxLandWidthPx

            tvLeft.layoutParams = tvLeft.layoutParams.apply {
                width = containerWidth
                (this as? ViewGroup.MarginLayoutParams)?.rightMargin = 0
            }

            val landscapeOffset = (calcRealGapPx(tvLeft) - DEFAULT_CAMERA_GAP_PX).toFloat()
            AnimUtils.applyTextWithAnim(tvLeft, title, config.animMode, landscapeOffset) {
                startMarquee(tvLeft, config)
            }
        }

        private fun applyPortraitSplitMode(
            tvLeft: TextView, tvRight: TextView,
            title: String, config: ModuleConfig,
            paint: android.graphics.Paint, totalWidthPx: Float,
            realGapPx: Int, offset: Float
        ) {
            val maxLeftPx = config.maxLeftWidth.toFloat()
            val leftCapPx = minOf(totalWidthPx / 2f, maxLeftPx)
            var splitIndex = paint.breakText(title, true, leftCapPx, null)

            if (splitIndex < title.length - splitIndex && splitIndex + 1 <= title.length) {
                if (paint.measureText(title, 0, splitIndex + 1) <= maxLeftPx) splitIndex++
            }
            splitIndex = splitIndex.coerceIn(0, title.length)

            splitIndex = adjustForWordBoundary(title, splitIndex, paint, maxLeftPx)

            val strLeft = title.take(splitIndex)
            val strRight = title.substring(splitIndex)
            val leftTextRawW = ceil(paint.measureText(strLeft)).toInt()
            val rightTextRawW = ceil(paint.measureText(strRight)).toInt()

            val rightDisplayWidth = if (rightTextRawW > config.maxLeftWidth) {
                val fitCount = paint.breakText(strRight, true, maxLeftPx, null)
                ceil(paint.measureText(strRight, 0, fitCount)).toInt()
            } else rightTextRawW

            val leftContentW = maxOf(leftTextRawW, rightDisplayWidth)
            tvLeft.layoutParams = tvLeft.layoutParams.apply { width = leftContentW + realGapPx }
            tvLeft.setPadding(0, 0, realGapPx, 0)
            tvRight.layoutParams = tvRight.layoutParams.apply { width = rightDisplayWidth }

            tvLeft.ellipsize = TextUtils.TruncateAt.END
            tvRight.ellipsize = if (config.marquee) TextUtils.TruncateAt.END else null

            AnimUtils.applyTextWithAnim(tvLeft, strLeft, config.animMode, offset) { }
            AnimUtils.applyTextWithAnim(
                tvRight,
                strRight,
                config.animMode,
                offset + RIGHT_EXTRA_OFFSET_PX
            ) {
                if (config.marquee && rightTextRawW > rightDisplayWidth) startMarquee(
                    tvRight,
                    config
                )
            }

            tvLeft.visibility = View.VISIBLE
            tvRight.visibility = View.VISIBLE
            tvLeft.bringToFront()
            tvRight.bringToFront()
        }

        private fun adjustForWordBoundary(
            text: String, originalIndex: Int,
            paint: android.graphics.Paint, maxLeftPx: Float
        ): Int {
            var idx = originalIndex
            if (idx <= 0 || idx >= text.length) return idx.coerceIn(0, text.length)

            val isAsciiAlnum = { c: Char -> c.isLetterOrDigit() && c.code < 128 }
            if (!isAsciiAlnum(text[idx - 1]) || !isAsciiAlnum(text[idx])) return idx

            var backSplit = idx
            while (backSplit > 0 && isAsciiAlnum(text[backSplit - 1])) backSplit--

            var forwardSplit = idx
            while (forwardSplit < text.length && isAsciiAlnum(text[forwardSplit])) forwardSplit++

            val forwardPx = paint.measureText(text, 0, forwardSplit)

            idx = if (backSplit > 0) {
                if (forwardPx <= maxLeftPx) forwardSplit else backSplit
            } else {
                if (forwardPx <= maxLeftPx) forwardSplit
                else paint.breakText(text, true, maxLeftPx, null)
            }
            return idx.coerceIn(0, text.length)
        }

        private fun startMarquee(textView: TextView, config: ModuleConfig) {
            textView.ellipsize = null
            textView.isSelected = false
            stopMarquee(textView)
            textView.post {
                if (textView.paint.measureText(textView.text.toString()) > textView.width) {
                    MarqueeController(textView, config.speed, config.delay).also {
                        scrollerMap[textView] = it
                        it.start()
                    }
                }
            }
        }

        private fun stopMarquee(textView: TextView) {
            scrollerMap.remove(textView)?.stop()
            textView.scrollTo(0, 0)
        }

        class MarqueeController(
            private val view: TextView,
            private val speedPxPerSec: Int,
            private val delayMs: Int
        ) : Choreographer.FrameCallback {

            private companion object {
                const val PAUSE_AT_END_MS = 1000
            }

            private var currentScrollX = 0f
            private var isRunning = false
            private var startTimeNanos = 0L
            private var lastFrameTimeNanos = 0L
            private val choreographer = Choreographer.getInstance()
            private var state = 0

            private var cachedTextWidth = 0f
            private var cachedMaxScroll = 0f

            fun start() {
                if (isRunning) return
                cachedTextWidth = view.paint.measureText(view.text.toString())
                cachedMaxScroll = max(0f, cachedTextWidth - view.width.toFloat())

                if (cachedMaxScroll <= 0) return

                isRunning = true
                currentScrollX = 0f
                state = 0
                startTimeNanos = 0
                choreographer.postFrameCallback(this)
            }

            fun stop() {
                isRunning = false
                choreographer.removeFrameCallback(this)
            }

            override fun doFrame(frameTimeNanos: Long) {
                if (!isRunning) return

                if (startTimeNanos == 0L) {
                    startTimeNanos = frameTimeNanos
                    lastFrameTimeNanos = frameTimeNanos
                }

                if (cachedMaxScroll <= 0) {
                    stop(); return
                }

                val elapsedMs = { (frameTimeNanos - startTimeNanos) / 1_000_000 }

                when (state) {
                    0 -> if (elapsedMs() >= delayMs) {
                        state = 1; lastFrameTimeNanos = frameTimeNanos
                    }

                    1 -> {
                        currentScrollX += speedPxPerSec * ((frameTimeNanos - lastFrameTimeNanos) / 1_000_000_000f)
                        if (currentScrollX >= cachedMaxScroll) {
                            currentScrollX = cachedMaxScroll; state = 2; startTimeNanos =
                                frameTimeNanos
                        }
                        view.scrollTo(currentScrollX.toInt(), 0)
                    }

                    2 -> if (elapsedMs() > PAUSE_AT_END_MS) {
                        currentScrollX = 0f; view.scrollTo(0, 0); state = 0; startTimeNanos =
                            frameTimeNanos
                    }
                }

                lastFrameTimeNanos = frameTimeNanos
                choreographer.postFrameCallback(this)
            }
        }

        private fun createBaseTextView(
            context: Context,
            viewTag: String,
            textSizeSp: Int
        ): TextView =
            TextView(context).apply {
                tag = viewTag
                setTextColor(Color.WHITE)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp.toFloat())
                includeFontPadding = false
                isSingleLine = true
                gravity = Gravity.CENTER_VERTICAL or Gravity.START
                ellipsize = null
                isSelected = false
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { gravity = Gravity.CENTER_VERTICAL or Gravity.START }
                elevation = 2000f
                translationZ = 2000f
            }

        private class MediaSessionTracker(context: Context) {
            private val manager =
                context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
            private var currentController: MediaController? = null

            private val cachedTitles = mutableMapOf<String, String>()

            var onTitleChanged: ((String, String) -> Unit)? = null

            private val mediaCallback = object : MediaController.Callback() {
                override fun onMetadataChanged(metadata: MediaMetadata?) {
                    try {
                        updateCachedTitle(currentController)
                    } catch (e: Exception) {
                        YLog.error(msg = "[HyperLyric] onMetadataChanged 异常: ${e.message}")
                    }
                }

                override fun onPlaybackStateChanged(state: PlaybackState?) {
                    try {
                        updateCachedTitle(currentController)
                    } catch (e: Exception) {
                        YLog.error(msg = "[HyperLyric] onPlaybackStateChanged 异常: ${e.message}")
                    }
                }

                override fun onSessionDestroyed() {
                    try {
                        refreshActiveSessions()
                    } catch (e: Exception) {
                        YLog.error(msg = "[HyperLyric] onSessionDestroyed 异常: ${e.message}")
                    }
                }
            }

            init {
                try {
                    manager.addOnActiveSessionsChangedListener(
                        { onActiveSessionsChanged(it) },
                        null
                    )
                    refreshActiveSessions()
                } catch (e: Exception) {
                    YLog.error(msg = "[HyperLyric] [E] MediaSessionTracker init 异常: ${e.message}，媒体监听失败")
                }
            }

            private fun refreshActiveSessions() {
                try {
                    onActiveSessionsChanged(manager.getActiveSessions(null))
                } catch (e: Exception) {
                    YLog.warn(msg = "[HyperLyric] [W] 刷新媒体会话失败: ${e.message}")
                }
            }

            private fun onActiveSessionsChanged(controllers: List<MediaController>?) {
                if (controllers.isNullOrEmpty()) return
                val target = controllers.find {
                    it.playbackState?.state == PlaybackState.STATE_PLAYING
                } ?: controllers.find {
                    it.metadata?.getString(MediaMetadata.METADATA_KEY_TITLE) != null
                } ?: controllers.first()

                if (currentController?.packageName != target.packageName) {
                    try {
                        currentController?.unregisterCallback(mediaCallback)
                    } catch (_: Exception) {
                    }
                    currentController = target
                    try {
                        target.registerCallback(mediaCallback)
                    } catch (_: Exception) {
                    }
                    updateCachedTitle(target)
                }
            }

            private fun updateCachedTitle(controller: MediaController?) {
                controller ?: return
                val pkg = controller.packageName ?: return
                val newTitle = (controller.metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)
                    ?: "").substringBefore("\n")
                val oldTitle = cachedTitles[pkg]
                cachedTitles[pkg] = newTitle

                if (oldTitle != newTitle) {
                    onTitleChanged?.invoke(pkg, newTitle)
                }
            }

            fun getSongTitle(targetPkg: String): String {
                cachedTitles[targetPkg]?.let { if (it.isNotEmpty()) return it }
                return try {
                    manager.getActiveSessions(null)
                        .firstOrNull { it.packageName == targetPkg }
                        ?.metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)
                        ?.substringBefore("\n")
                        ?.also { if (it.isNotEmpty()) cachedTitles[targetPkg] = it }
                        ?: ""
                } catch (_: Exception) {
                    ""
                }
            }
        }
    }

    private fun isLandscape(context: Context): Boolean =
        context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    private fun dp2px(context: Context, dp: Float): Int =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
