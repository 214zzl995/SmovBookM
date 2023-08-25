package com.leri.smovbook.ui.smovDetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leri.smovbook.ui.theme.SmovBookMTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmovDetailAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    title: String,
    onBack: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {

    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                title,
                maxLines = 1,
                fontSize = 18.sp,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "Localized description"
                )
            }
        },
        actions = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Localized description"
                )
            }
            actions()
        },
        scrollBehavior = scrollBehavior
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AppBarPreview() {
    SmovBookMTheme(isDarkTheme = false) {
        SmovDetailAppBar(title = "SmovBook")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AppBarPreviewDark() {
    SmovBookMTheme(isDarkTheme = true) {
        SmovDetailAppBar(title = "SmovBook")
    }
}
