package com.leri.smovbook.ui.refactor

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
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = 10086L) {
        viewModel.fetchPersonDetailsById(10086L)
    }

    val person by viewModel.personFlow.collectAsState(initial = null)

    IconButton(onClick = {
        viewModel.fetchPersonDetailsById(10086L)
        viewModel.changeUrlTest("192.168.88.28:8000")
    }, modifier = modifier) {
        Icon(Icons.Filled.Check, contentDescription = "Localized description")
    }

    Text(text = person ?: "")


}