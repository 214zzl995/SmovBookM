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

@OptIn(ExperimentalMaterial3Api::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val smovRepository: SmovRepository
) : ViewModel() {

    private val _detailOpen: MutableState<DrawerValue> = mutableStateOf(DrawerValue.Closed)
    val detailOpen: State<DrawerValue> get() = _detailOpen

    val smovsState: State<HomeViewModelState> = mutableStateOf(HomeViewModelState())

    private val smovPageState: MutableStateFlow<Int> = MutableStateFlow(0)

    private val _smovLoadingState: MutableState<NetworkState> = mutableStateOf(NetworkState.IDLE)
    val smovLoadingState: State<NetworkState> get() = _smovLoadingState

    private val newSmovFlow = smovPageState.flatMapLatest {
        _smovLoadingState.value = NetworkState.LOADING
        if (it == -1) {
            flow {
                listOf<Smov>()
            }
        } else {
            delay(500)
            smovRepository.getSmovPagination(
                pageNum = it,
                pageSize = 500,
                success = { _smovLoadingState.value = NetworkState.SUCCESS },
                error = { _smovLoadingState.value = NetworkState.ERROR }
            )
        }

    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    val smovServerUrl: MutableStateFlow<String> = MutableStateFlow("")

    val smovHistoryUrl = smovServerUrl.flatMapLatest {
        smovRepository.getSmovHistoryUrl()
    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    init {
        Timber.d("Injection HomeViewModel")
        viewModelScope.launch(Dispatchers.IO) {
            //这个会触发 newSmovFlow 的获取 当修改smovPageState时也会触发
            newSmovFlow.collectLatest {
                smovsState.value.smovs.addAll(it)
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            smovRepository.getSmovServiceUrlAndPort().map { if (it == ":0") "127.0.0.1:443" else it }.collectLatest {
                smovServerUrl.value = it

            }
        }
    }

    fun fetchNextSmovPage() {
        smovPageState.value++
    }

    private fun fetchSmovPageForNum(page_num: Int) {
        smovPageState.value = page_num
    }

    fun changeServerUrl(url: String) {
        //这个change 可能是有性能问题 测试后发现 只需要几毫秒 不影响
        Timber.d("修改url为$url")
        smovRepository.changeSmovServiceUrl(url)
        smovServerUrl.value = url
        Timber.d("查看当前url为为${smovServerUrl.value}")
        //刷新数据
        refreshData()
    }

    fun refreshData() {
        //直接清除数据会造成 白屏 说实话 不舒服
        smovsState.value.smovs.removeAll { true }

        if (smovPageState.value == 0) {
            //先更新为-1 因为防抖无法传0 这个防抖会造成 -1 比0更早实现 就会造成空数据
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


