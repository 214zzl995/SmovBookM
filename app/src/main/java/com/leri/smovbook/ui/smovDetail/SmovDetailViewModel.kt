package com.leri.smovbook.ui.smovDetail

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leri.smovbook.models.network.NetworkState
import com.leri.smovbook.repository.SmovRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

@HiltViewModel
class SmovDetailViewModel @Inject constructor(
    private val smovRepository: SmovRepository,
) : ViewModel() {

    private val smovIdSharedFlow: MutableSharedFlow<Long> = MutableSharedFlow(replay = 1)

    private val _pageState: MutableState<NetworkState> = mutableStateOf(NetworkState.IDLE)
    val pageState: State<NetworkState> get() = _pageState

    val smovFlow = smovIdSharedFlow.flatMapLatest {
        _pageState.value = NetworkState.LOADING
        smovRepository.getSmovById(it,
            success = { _pageState.value = NetworkState.SUCCESS },
            error = { _pageState.value = NetworkState.ERROR }
        )
    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    fun fetchSmovDetailsById(id: Long) = smovIdSharedFlow.tryEmit(id)
}