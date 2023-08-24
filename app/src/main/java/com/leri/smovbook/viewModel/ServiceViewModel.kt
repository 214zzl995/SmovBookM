package com.leri.smovbook.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leri.smovbook.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository,
) : ViewModel() {

    private val _serverUrl: MutableState<String> = mutableStateOf("")

    private val _historyUrl: MutableState<List<String>> = mutableStateOf(emptyList())

    init {
        Timber.d("Injection ServiceVIewModel")

        viewModelScope.launch(Dispatchers.IO) {
            serviceRepository.getSmovServiceUrlAndPort()
                .collectLatest {
                    _serverUrl.value = it
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            serviceRepository.getSmovHistoryUrl()
                .collectLatest {
                    _historyUrl.value = it
                }
        }
    }

    fun changeServiceUrl(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            serviceRepository.changeSmovServiceUrl(url)
        }
    }


    val serverUrl: State<String> get() = _serverUrl

    val historyUrl: State<List<String>> get() = _historyUrl
}