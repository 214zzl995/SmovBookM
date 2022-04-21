package com.leri.smovbook.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SmovContentViewModel : ViewModel() {

    private val _serverUrl = MutableStateFlow("127.0.0.1")

    val serverUrl: StateFlow<String> = _serverUrl

    fun settServer(ServerUrl: String) {
        _serverUrl.value = ServerUrl
    }

    fun getServer(): String {
        return _serverUrl.value.ifBlank {
            "127.0.0.1"
        }

    }
}