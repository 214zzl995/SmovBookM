package com.leri.smovbook.ui.refactor

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

/**
 * @Description: XXX
 * @author DingWei
 * @version 12/8/2022 下午3:29
 */
@Composable
fun Refactor(
    viewModel: RefactorViewModel,
    modifier: Modifier = Modifier
) {

    //得出结论 想要让他获取数据必须要 在这个Composable 有定义  val person by viewModel.personFlow.collectAsState(initial = null) 且让数据变化 SharedFlow 无需默认值
    LaunchedEffect(key1 = 10086L) {
        viewModel.fetchPersonDetailsById(10086L)
    }

    val person by viewModel.personFlow.collectAsState(initial = null)
    //val smovFlow by viewModel.smovFlow.collectAsState(initial = null)

    IconButton(onClick = {
        viewModel.fetchNextMoviePage()

    }, modifier = modifier) {
        Icon(Icons.Filled.Check, contentDescription = "Localized description")
    }

    val ss = viewModel.smovStateFlow.value

    Text(text = ss.toString())

    Text(text = person ?: "")


}