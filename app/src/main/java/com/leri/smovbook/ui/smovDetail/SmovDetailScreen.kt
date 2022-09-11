package com.leri.smovbook.ui.smovDetail


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.leri.smovbook.models.entities.Smov
import com.leri.smovbook.ui.data.testDataSin

//参考https://github.com/raheemadamboev/online-video-player
//参考https://github.com/halilozercan/ComposeVideoPlayer
//参考https://github.com/topics/jetpack-compose?q=player
//参考https://exoplayer.dev/
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SmovDetailScreen(
    smov: Smov,
    smovName: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior() }

    val contentPadding = WindowInsets.statusBarsIgnoringVisibility.asPaddingValues()

    Scaffold(
        modifier = modifier
            .windowInsetsPadding(
                WindowInsets
                    .navigationBars
                    .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
            ),
        topBar = {
            SmovDetailAppBar(
                scrollBehavior = scrollBehavior,
                title = smovName,
                onBack = onBack,
                modifier = Modifier.padding(contentPadding)
            )
        }
    ) { innerPadding ->
        println(innerPadding)
    }


}

@Preview
@Composable
fun SmovDetailScreenPreview() {
    SmovDetailScreen(smov = testDataSin, smovName = "SmovBook", onBack = { })
}





