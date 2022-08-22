package com.leri.smovbook.ui.refactor

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leri.smovbook.models.entities.Smov
import com.leri.smovbook.models.network.NetworkState
import com.leri.smovbook.repository.SmovRepository
import com.leri.smovbook.util.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed interface HomeUiState {

    val errorMessages: List<ErrorMessage>

    data class NoData(
        override val errorMessages: List<ErrorMessage>,
    ) : HomeUiState

    data class HasData(
        override val errorMessages: List<ErrorMessage>,
        val smovs: MutableList<Smov>,
        val selectedSmov: Smov?
    ) : HomeUiState
}

data class HomeViewModelState(
    val smovs: MutableList<Smov> = mutableListOf(),
    val errorMessages: List<ErrorMessage> = emptyList(),
    val selectedSmovId: Int = 0,
    val isDetailOpen: Boolean = false,
) {
    fun toUiState(): HomeUiState =
        if (smovs.isEmpty()) {
            HomeUiState.NoData(
                errorMessages = errorMessages,
            )
        } else {
            HomeUiState.HasData(
                smovs = smovs,
                errorMessages = errorMessages,
                selectedSmov = smovs.find {
                    it.id == selectedSmovId
                },
            )
        }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val smovRepository: SmovRepository
) : ViewModel() {

    val smovsState: State<HomeViewModelState> = mutableStateOf(HomeViewModelState())

    private val smovPageState: MutableStateFlow<Int> = MutableStateFlow(0)

    private val _smovLoadingState: MutableState<NetworkState> = mutableStateOf(NetworkState.IDLE)

    val smovLoadingState: State<NetworkState> get() = _smovLoadingState

    private val newSmovFlow = smovPageState.flatMapLatest {
        _smovLoadingState.value = NetworkState.LOADING
        smovRepository.getSmovPagination(
            pageNum = it,
            pageSize = 10,
            success = { _smovLoadingState.value = NetworkState.SUCCESS },
            error = { _smovLoadingState.value = NetworkState.ERROR }
        )
    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    private val _smovServerUrl: MutableStateFlow<String> = MutableStateFlow("")

    val smovServerUrl: StateFlow<String> = _smovServerUrl

    val smovHistoryUrl = _smovServerUrl.flatMapLatest {
        smovRepository.getSmovHistoryUrl()
    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    init {
        Timber.d("Injection HomeViewModel")
        viewModelScope.launch(Dispatchers.IO) {
            //这个回触发 newSmovFlow 的获取 当修改smovPageState时也会触发
            newSmovFlow.collectLatest {
                smovsState.value.smovs.addAll(it)
            }
        }



        viewModelScope.launch(Dispatchers.IO) {

            Timber.d("测试测试测试")

            val s = smovRepository.getSmovServiceUrlAndPort()

            Timber.d("测试测试测试" + s.count())

            s.collectLatest {
                _smovServerUrl.value = it
            }

            Timber.d("测试测试测试" + _smovServerUrl.value)

            //_smovServerUrl.value = smovRepository.getSmovServiceUrlAndPort().first()

        }
    }

    fun fetchNextSmovPage() {
        smovPageState.value++
    }

    fun changeServerUrl(url: String) {
        smovRepository.changeSmovServiceUrl(url)
        _smovServerUrl.value = url
    }


}