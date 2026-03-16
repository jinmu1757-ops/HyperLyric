package com.lidesheng.hyperlyric.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.lidesheng.hyperlyric.model.DynamicLyricData
import com.lidesheng.hyperlyric.utils.ThemeUtils
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.Delete
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

val commonMusicApps = mapOf(
    "com.salt.music" to "Salt Player",
    "com.netease.cloudmusic" to "网易云音乐",
    "com.tencent.qqmusic" to "QQ音乐",
    "cn.kuwo.player" to "酷我音乐",
    "com.kugou.android" to "酷狗音乐",
    "com.apple.android.music" to "Apple Music",
    "com.spotify.music" to "Spotify",
    "cmccwm.mobilemusic" to "咪咕音乐",
    "com.luna.music" to "汽水音乐",
    "com.kugou.android.lite" to "酷狗音乐概念版",
    "com.google.android.apps.youtube.music" to "YouTube Music",
    "cn.wenyu.bodian" to "波点音乐",
    "com.miui.player" to "小米音乐",
    "com.xuncorp.qinalt.music" to "青盐云听"
)

class WhitelistActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false

        setContent {
            ThemeUtils.MiuixThemeWrapper {
                WhitelistScreen(onBack = { finish() })
            }
        }
    }
}

@Composable
fun WhitelistScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    
    val hazeState = remember { HazeState() }
    val hazeStyle = HazeStyle(
        backgroundColor = MiuixTheme.colorScheme.surface,
        tint = HazeTint(MiuixTheme.colorScheme.surface.copy(0.8f))
    )

    LaunchedEffect(Unit) {
        DynamicLyricData.initWhitelist(context)
    }

    val whitelistSet by DynamicLyricData.whitelistState.collectAsState()
    val whitelist = remember(whitelistSet) { whitelistSet.toList() }


    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var tempInputPackage by remember { mutableStateOf("") }
    var packageToDelete by remember { mutableStateOf("") }


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                color = Color.Transparent,
                title = "自定义白名单",
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
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
        SuperDialog(
            title = "输入应用包名",
            show = showAddDialog,
            onDismissRequest = { showAddDialog = false }
        ) {
            Column {
                TextField(
                    value = tempInputPackage,
                    onValueChange = { tempInputPackage = it },
                    label = "例如 com.netease.cloudmusic",
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        text = "取消",
                        onClick = { showAddDialog = false },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    TextButton(
                        text = "保存",
                        colors = ButtonDefaults.textButtonColorsPrimary(),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (tempInputPackage.isNotBlank()) {
                                val success =
                                    DynamicLyricData.addPackageToWhitelist(context, tempInputPackage)
                                if (success) {
                                    showAddDialog = false
                                } else {
                                    Toast.makeText(context, "该应用已存在", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "包名不能为空", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }

        SuperDialog(
            title = "确认从白名单中移除应用吗？",
            show = showDeleteDialog,
            onDismissRequest = { showDeleteDialog = false }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    text = "取消",
                    onClick = { showDeleteDialog = false },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(20.dp))
                TextButton(
                    text = "确认",
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        DynamicLyricData.removePackageFromWhitelist(context, packageToDelete)
                        showDeleteDialog = false
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .scrollEndHaptic()
                .hazeSource(state = hazeState)
                .overScrollVertical(),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding(),
                start = 12.dp,
                end = 12.dp,
                bottom = padding.calculateBottomPadding()
            )
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    SuperArrow(
                        title = "添加白名单应用",
                        onClick = {
                            tempInputPackage = ""
                            showAddDialog = true
                        },
                        holdDownState = showAddDialog
                    )
                }
            }

            item {
                SmallTitle(
                    text = "已添加的应用",
                    insideMargin = PaddingValues(10.dp, 4.dp)
                )
            }

            item {
                if (whitelist.isNotEmpty()) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            whitelist.forEachIndexed { _, packageName ->
                                val appName = commonMusicApps[packageName]
                                BasicComponent(
                                    title = appName ?: packageName,
                                    summary = if (appName != null) packageName else null,
                                    endActions = {
                                        IconButton(
                                            onClick = {
                                                packageToDelete = packageName
                                                showDeleteDialog = true
                                            }
                                        ) {
                                            Icon(
                                                imageVector = MiuixIcons.Delete,
                                                contentDescription = "删除",
                                                tint = MiuixTheme.colorScheme.onSurfaceVariantActions,
                                            )
                                        }
                                    },
                                    onClick = {
                                        packageToDelete = packageName
                                        showDeleteDialog = true
                                    },
                                    holdDownState = showDeleteDialog && packageToDelete == packageName
                                )
                            }
                        }
                    }
                } else {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        top.yukonga.miuix.kmp.basic.Text(
                            text = "暂无白名单应用",
                            color = MiuixTheme.colorScheme.onSurfaceSecondary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
