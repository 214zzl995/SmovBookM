package com.leri.smovbook.ui.setting

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.google.accompanist.flowlayout.FlowRow
import com.leri.smovbook.R
import com.leri.smovbook.config.ThirdPartyPlayer
import com.leri.smovbook.ui.theme.SmovBookMTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    thirdPartyPlayer: ThirdPartyPlayer,
    setThirdPartyPlayer: (ThirdPartyPlayer) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var openBottomSheet by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        "设置",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    Row(modifier = Modifier.padding(start = 11.dp)) {
                        Icon(
                            modifier = Modifier.height(24.dp),
                            imageVector = Icons.Outlined.Settings,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            contentDescription = stringResource(id = R.string.info)
                        )
                    }
                },
                actions = {},
                scrollBehavior = scrollBehavior
            )
        },
        content = { innerPadding ->

            AppSelectBottomSheet(
                openBottomSheet,
                { openBottomSheet = false },
                getVideoPlayerApps(),
                setThirdPartyPlayer
            )

            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    SettingsTitle(text = "系统")
                }
                item {
                    SettingsItem(
                        text = "默认打开应用",
                        description = thirdPartyPlayer.thirdPartyPlayerName,
                        icon = Icons.Outlined.Android,
                        onClick = {
                            openBottomSheet = true
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun SettingsTitle(
    text: String,
) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp, bottom = 5.dp),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun SettingsItem(
    text: String,
    icon: ImageVector,
    description: String = "",
    onClick: () -> Unit,
    action: @Composable () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable(onClick = onClick)
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = text, Modifier.size(35.dp))
            Column(modifier = Modifier.padding(start = 20.dp)) {
                Text(
                    text = text,
                    modifier = Modifier,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (description != "") {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

            }
        }

        Box(modifier = Modifier) {
            action()
        }

    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSelectBottomSheet(
    openBottomSheet: Boolean,
    closeBottomSheet: () -> Unit,
    appList: List<ResolveInfo>,
    setThirdPartyPlayer: (ThirdPartyPlayer) -> Unit,
) {
    val content = LocalContext.current
    val packageManager = remember { content.packageManager }

    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberSheetState(
        skipHalfExpanded = true
    )

    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = closeBottomSheet,
            sheetState = bottomSheetState,
        ) {
            Text(
                text = "选择默认打开应用",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 20.dp)
            )
            Box(
                modifier = Modifier
                    .height(280.dp)
                    .padding(all = 20.dp)
            ) {

                FlowRow {
                    Column(
                        modifier = Modifier
                            .width(80.dp)
                            .padding(top = 4.dp, bottom = 4.dp, end = 12.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember {
                                    MutableInteractionSource()
                                }) {
                                val thirdPartyPlayer = ThirdPartyPlayer
                                    .newBuilder()
                                    .setThirdPartyPlayerPackage("")
                                    .setThirdPartyPlayerName("")
                                    .build()

                                setThirdPartyPlayer(thirdPartyPlayer)
                                scope.launch {
                                    bottomSheetState.hide()
                                    closeBottomSheet()
                                }
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.ic_smov_ico),
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .drawWithContent {
                                    drawContent()
                                },
                        )

                        Text(
                            text = "空",
                            modifier = Modifier.padding(top = 5.dp),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }
                    for (app in appList) {
                        Column(
                            modifier = Modifier
                                .width(80.dp)
                                .padding(top = 4.dp, bottom = 4.dp, end = 12.dp)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember {
                                        MutableInteractionSource()
                                    }) {
                                    val thirdPartyPlayer = ThirdPartyPlayer
                                        .newBuilder()
                                        .setThirdPartyPlayerPackage(app.activityInfo.packageName)
                                        .setThirdPartyPlayerName(
                                            app
                                                .loadLabel(packageManager)
                                                .toString()
                                        )
                                        .build()

                                    setThirdPartyPlayer(thirdPartyPlayer)
                                    scope.launch {
                                        bottomSheetState.hide()
                                        closeBottomSheet()
                                    }
                                },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val bitmap = app.loadIcon(packageManager).toBitmap().asImageBitmap()
                            Image(
                                bitmap = bitmap,
                                contentDescription = null,
                                modifier = Modifier.size(50.dp),
                            )
                            Text(
                                text = app.loadLabel(packageManager).toString(),
                                modifier = Modifier.padding(top = 5.dp),
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                }
            }
        }
    }

}

@Composable
private fun getVideoPlayerApps(): List<ResolveInfo> {
    val content = LocalContext.current
    val packageManager = remember { content.packageManager }
    val intent = Intent(Intent.ACTION_VIEW)
    intent.type = "video/*"
    intent.addCategory(Intent.CATEGORY_BROWSABLE)
    return packageManager.queryIntentActivities(intent, 0)
}


@Preview
@Composable
fun BarCodeScanTitlePreview() {
    SmovBookMTheme() {
        Surface {
            Column {
                SettingsItem(
                    text = "默认打开应用",
                    description = "某某应用",
                    icon = Icons.Outlined.Android,
                    onClick = {}
                ) {
                    Switch(checked = false, onCheckedChange = {})
                }
            }
        }
    }
}

@Preview
@Composable
fun BarCodeScanTitleUndPreview() {
    SmovBookMTheme() {
        Surface {
            Column {
                SettingsItem(
                    text = "默认打开应用",
                    icon = Icons.Outlined.Android,
                    onClick = {}
                ) {
                    Switch(checked = false, onCheckedChange = {})
                }
            }
        }
    }
}



