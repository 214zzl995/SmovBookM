package com.leri.smovbook.ui.components

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private enum class Visibility {
    VISIBLE,
    GONE
}

@Composable
fun JumpToTop(
    enabled: Boolean,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Show Jump to Bottom button
    val transition = updateTransition(
        if (enabled) Visibility.VISIBLE else Visibility.GONE,
        label = ""
    )
    val bottomOffset by transition.animateDp(label = "") {
        if (it == Visibility.GONE) {  //判断滑动位置 尝试着当下滑后 出现按钮 当在最上面 不出现按钮 尝试修改
            (-32).dp
        } else {
            32.dp
        }
    }
    if (bottomOffset > 0.dp) {
        FloatingActionButton(
            onClick = onClicked,
            modifier = modifier
                .padding(16.dp)
                .navigationBarsPadding()
                .offset(x = 0.dp, y = -bottomOffset)
                .height(48.dp)
                ,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,

        ) {
            Icon(
                imageVector = Icons.Filled.ArrowDownward,
                modifier = Modifier.height(24.dp).rotate(180f),
                contentDescription = null
            )
        }
    }
}

@Preview
@Composable
fun JumpToBottomPreview() {
    JumpToTop(enabled = true, onClicked = {})
}
