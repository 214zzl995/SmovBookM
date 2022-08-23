package com.leri.smovbook.ui.refactor

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier


/**
 * @Description: XXX
 * @author DingWei
 * @version 12/8/2022 下午3:29
 */
@Composable
fun Refactor(
    viewModel: RefactorViewModel,
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {

    //得出结论 想要让他获取数据必须要 在这个Composable 有定义  val person by viewModel.personFlow.collectAsState(initial = null) 且让数据变化 SharedFlow 无需默认值

    IconButton(onClick = {
        homeViewModel.changeServerUrl("126.22.22.1:1111")
    }, modifier = modifier) {
        Icon(Icons.Filled.Check, contentDescription = "Localized description")
    }

    val ss = viewModel.smovStateFlow.value

    Text(text = ss.toString())

    val serverUrl by homeViewModel.smovServerUrl.collectAsState()

    val historyUrl by homeViewModel.smovHistoryUrl.collectAsState(initial = mutableListOf())

    Text("ServerUrl$serverUrl")

    for (his in historyUrl) {
        Text(his)
    }

    val smovs by homeViewModel.smovsState

    when (smovs.toUiState()) {
        is HomeUiState.HasData -> {
            for (s in smovs.smovs) {
                Text(s.name)
            }
        }

        is HomeUiState.NoData -> {

        }
    }
}
