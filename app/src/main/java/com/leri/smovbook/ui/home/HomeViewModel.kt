package com.leri.smovbook.ui.home

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leri.smovbook.models.entities.Smov
import com.leri.smovbook.models.network.NetworkState
import com.leri.smovbook.repository.SmovRepository
import com.leri.smovbook.util.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed interface HomeUiState {

    val errorMessages: List<ErrorMessage>
    val dataState: Boolean

    data class NoData(
        override val errorMessages: List<ErrorMessage>, override val dataState: Boolean = false,
    ) : HomeUiState

    data class HasData(
        override val errorMessages: List<ErrorMessage>,
        override val dataState: Boolean = true,
        val smovs: MutableList<Smov>,
        val selectedSmov: Smov?,
    ) : HomeUiState

}

data class HomeViewModelState(
    val smovs: MutableList<Smov> = mutableListOf(),
    val errorMessages: MutableList<ErrorMessage> = mutableListOf(),
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

data class ServerState(
    var serverUrl: String = "",
    var historyUrl: MutableList<String> = mutableListOf(),
    val changeServerUrl: (String) -> Unit = {},
) {
    fun update(state: ServerState) {
        serverUrl = state.serverUrl
        historyUrl = state.historyUrl
    }

    constructor(changeServerUrl: (String) -> Unit) : this("", mutableListOf(), changeServerUrl)

}


/**
 * 有大量设计不合理的位置 主要是刷新的处理和刚开始的处理
 * 刷新处理: 应该删除所有数据后 将当前数据重新刷入
 * 刚开始的处理 不知道为什么没有检测到数据的变动
 */
@OptIn(ExperimentalMaterial3Api::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val smovRepository: SmovRepository,
) : ViewModel() {

    private val _detailOpen: MutableState<DrawerValue> = mutableStateOf(DrawerValue.Closed)
    val detailOpen: State<DrawerValue> get() = _detailOpen

    val smovsState: State<HomeViewModelState> = mutableStateOf(HomeViewModelState())

    private val smovPageState: MutableStateFlow<Int> = MutableStateFlow(0)

    private val _pageState: MutableState<NetworkState> = mutableStateOf(NetworkState.IDLE)
    val pageState: State<NetworkState> get() = _pageState

    private val newSmovFlow = smovPageState.flatMapLatest {
        _pageState.value = NetworkState.LOADING

        if (it == -1) {
            flow {
                listOf<Smov>()
            }
        } else {
            delay(1000)
            smovRepository.getSmovPagination(
                pageNum = it,
                pageSize = 500,
                success = { _pageState.value = NetworkState.SUCCESS },
                error = { _pageState.value = NetworkState.ERROR }
            )
        }

    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    val serverState: State<ServerState> =
        mutableStateOf(ServerState {
            changeServerUrl(it)
        })

    init {
        Timber.d("Injection HomeViewModel")
        viewModelScope.launch(Dispatchers.IO) {
            newSmovFlow.collectLatest {
                smovsState.value.smovs.addAll(it)
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            smovRepository.getServerState()
                .map {
                    if (it.serverUrl == ":0") ServerState(
                        "127.0.0.1:8080",
                        it.historyUrl
                    ) else it
                }
                .collectLatest {
                    serverState.value.update(it)
                }
        }
    }

    fun fetchNextSmovPage() {
        if (pageState.value != NetworkState.LOADING) {
            smovPageState.value++
        }
    }

    private fun fetchSmovPageForNum(page_num: Int) {
        smovPageState.value = page_num
    }

    fun changeServerUrl(url: String) {
        //更新url前停止所有请求
        smovRepository.cancelAll()
        smovRepository.changeSmovServiceUrl(url)
        refreshData()
    }

    fun refreshData() {
        if (smovPageState.value == 0) {
            //先更新为-1 因为防抖无法传0 这个防抖会造成 -1 比0更早实现 就会造成空数据
            smovsState.value.smovs.removeAll { true }
            fetchSmovPageForNum(-1)
        }
        fetchSmovPageForNum(0)
    }

    fun errorShown(errorId: Long) {
        smovsState.value.errorMessages.removeIf {
            it.id == errorId
        }
    }


}


