package com.lidesheng.hyperlyric.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.lidesheng.hyperlyric.Constants
import com.lidesheng.hyperlyric.utils.ThemeUtils
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import org.json.JSONObject
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BackupActivity : ComponentActivity() {

    private val prefs by lazy {
        getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false

        setContent {
            ThemeUtils.MiuixThemeWrapper {
                BackupScreen()
            }
        }
    }


    private fun buildBackupJson(): String {
        val config = JSONObject().apply {
            put(Constants.KEY_TEXT_SIZE, prefs.getInt(Constants.KEY_TEXT_SIZE, Constants.DEFAULT_TEXT_SIZE))
            put(Constants.KEY_MAX_LEFT_WIDTH, prefs.getInt(Constants.KEY_MAX_LEFT_WIDTH, Constants.DEFAULT_MAX_LEFT_WIDTH))
            put(Constants.KEY_MARQUEE_MODE, prefs.getBoolean(Constants.KEY_MARQUEE_MODE, Constants.DEFAULT_MARQUEE))
            put(Constants.KEY_MARQUEE_SPEED, prefs.getInt(Constants.KEY_MARQUEE_SPEED, Constants.DEFAULT_MARQUEE_SPEED))
            put(Constants.KEY_MARQUEE_DELAY, prefs.getInt(Constants.KEY_MARQUEE_DELAY, Constants.DEFAULT_MARQUEE_DELAY))
            put(Constants.KEY_HIDE_NOTCH, prefs.getBoolean(Constants.KEY_HIDE_NOTCH, Constants.DEFAULT_HIDE_NOTCH))
            put(Constants.KEY_ANIM_MODE, prefs.getInt(Constants.KEY_ANIM_MODE, Constants.DEFAULT_ANIM_MODE))
            put(Constants.KEY_ONLINE_LYRIC_CACHE_LIMIT, prefs.getInt(Constants.KEY_ONLINE_LYRIC_CACHE_LIMIT, Constants.DEFAULT_ONLINE_LYRIC_CACHE_LIMIT))
            put(Constants.KEY_NOTIFICATION_CLICK_ACTION, prefs.getInt(Constants.KEY_NOTIFICATION_CLICK_ACTION, Constants.DEFAULT_NOTIFICATION_CLICK_ACTION))
            val whitelist = prefs.getStringSet(Constants.KEY_WHITELIST, emptySet()) ?: emptySet()
            put(Constants.KEY_WHITELIST, whitelist.joinToString(","))
        }

        val root = JSONObject().apply {
            put("version", 1)
            put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            put("config", config)
        }
        return root.toString(2)
    }

    private fun restoreFromJson(json: String): Boolean {
        return try {
            val root = JSONObject(json)
            val version = root.optInt("version", -1)
            if (version < 1) return false

            val config = root.getJSONObject("config")

            prefs.edit {
                if (config.has(Constants.KEY_TEXT_SIZE))
                    putInt(Constants.KEY_TEXT_SIZE, config.getInt(Constants.KEY_TEXT_SIZE).coerceIn(8, 27))
                if (config.has(Constants.KEY_MAX_LEFT_WIDTH))
                    putInt(Constants.KEY_MAX_LEFT_WIDTH, config.getInt(Constants.KEY_MAX_LEFT_WIDTH).coerceIn(40, 280))
                if (config.has(Constants.KEY_MARQUEE_SPEED))
                    putInt(Constants.KEY_MARQUEE_SPEED, config.getInt(Constants.KEY_MARQUEE_SPEED).coerceIn(10, 200))
                if (config.has(Constants.KEY_MARQUEE_DELAY))
                    putInt(Constants.KEY_MARQUEE_DELAY, config.getInt(Constants.KEY_MARQUEE_DELAY).coerceIn(0, 5000))
                if (config.has(Constants.KEY_ANIM_MODE))
                    putInt(Constants.KEY_ANIM_MODE, config.getInt(Constants.KEY_ANIM_MODE).coerceIn(0, 4))
                if (config.has(Constants.KEY_ONLINE_LYRIC_CACHE_LIMIT))
                    putInt(Constants.KEY_ONLINE_LYRIC_CACHE_LIMIT, config.getInt(Constants.KEY_ONLINE_LYRIC_CACHE_LIMIT).coerceIn(1, 1000))
                if (config.has(Constants.KEY_NOTIFICATION_CLICK_ACTION))
                    putInt(Constants.KEY_NOTIFICATION_CLICK_ACTION, config.getInt(Constants.KEY_NOTIFICATION_CLICK_ACTION).coerceIn(0, 2))
                if (config.has(Constants.KEY_MARQUEE_MODE))
                    putBoolean(Constants.KEY_MARQUEE_MODE, config.optBoolean(Constants.KEY_MARQUEE_MODE, Constants.DEFAULT_MARQUEE))
                if (config.has(Constants.KEY_HIDE_NOTCH))
                    putBoolean(Constants.KEY_HIDE_NOTCH, config.optBoolean(Constants.KEY_HIDE_NOTCH, Constants.DEFAULT_HIDE_NOTCH))
                if (config.has(Constants.KEY_WHITELIST)) {
                    val raw = config.optString(Constants.KEY_WHITELIST, "")
                    val set = if (raw.isBlank()) emptySet() else raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
                    putStringSet(Constants.KEY_WHITELIST, set)
                }
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    @Composable
    fun BackupScreen() {
        val context = LocalContext.current
        val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())

        val hazeState = remember { HazeState() }
        val hazeStyle = HazeStyle(
            backgroundColor = MiuixTheme.colorScheme.surface,
            tint = HazeTint(MiuixTheme.colorScheme.surface.copy(0.8f))
        )

        val backupLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("application/json"),
            onResult = { uri ->
                if (uri == null) return@rememberLauncherForActivityResult
                try {
                    val jsonBytes = buildBackupJson().toByteArray(Charsets.UTF_8)
                    val output = contentResolver.openOutputStream(uri)
                    if (output != null) {
                        output.use { it.write(jsonBytes); it.flush() }
                        Toast.makeText(this@BackupActivity, "备份成功", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@BackupActivity, "备份失败: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )

        val restoreLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument(),
            onResult = { uri ->
                if (uri == null) return@rememberLauncherForActivityResult
                try {
                    val json = context.contentResolver.openInputStream(uri)?.use { input ->
                        input.bufferedReader(Charsets.UTF_8).readText()
                    } ?: ""
                    if (json.isBlank()) {
                        Toast.makeText(context, "文件内容为空", Toast.LENGTH_SHORT).show()
                        return@rememberLauncherForActivityResult
                    }
                    val success = restoreFromJson(json)
                    Toast.makeText(
                        context,
                        if (success) "恢复成功，请重启应用" else "文件格式无效",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (_: Exception) {
                    Toast.makeText(context, "恢复失败", Toast.LENGTH_SHORT).show()
                }
            }
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    color = Color.Transparent,
                    title = "备份与恢复",
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        IconButton(
                            onClick = { finish() },
                            modifier = Modifier.padding(start = 12.dp)
                        ) {
                            Icon(imageVector = MiuixIcons.Back, contentDescription = "返回")
                        }
                    },
                    modifier = Modifier.hazeEffect(hazeState) {
                        style = hazeStyle
                        blurRadius = 25.dp
                        noiseFactor = 0f
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .scrollEndHaptic()
                    .hazeSource(state = hazeState)
                    .overScrollVertical()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                contentPadding = PaddingValues(
                    top = padding.calculateTopPadding(),
                    start = 12.dp,
                    end = 12.dp,
                    bottom = padding.calculateBottomPadding() + 16.dp
                ),
            ) {
                item {
                    SmallTitle(
                        text = "配置管理",
                        insideMargin = PaddingValues(10.dp, 4.dp)
                    )
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            SuperArrow(
                                title = "备份",
                                onClick = {
                                    val dateTime = LocalDateTime.now()
                                        .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"))
                                    backupLauncher.launch("hyperlyric_backup_$dateTime.json")
                                }
                            )
                            SuperArrow(
                                title = "恢复",
                                onClick = {
                                    restoreLauncher.launch(arrayOf("application/json"))
                                }
                            )
                        }
                    }

                    SmallTitle(
                        text = "调试信息",
                        insideMargin = PaddingValues(10.dp, 4.dp)
                    )
                    Card(modifier = Modifier.fillMaxWidth()) {
                        SuperArrow(
                            title = "查看模块日志",
                            onClick = {
                                context.startActivity(android.content.Intent(context, LogActivity::class.java))
                            }
                        )
                    }
                }
            }
        }
    }
}
