package com.leri.smovbook.viewModel

import android.util.Log
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


@OptIn(ExperimentalMaterial3Api::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val smovRepository: SmovRepository,
) : ViewModel() {

    private val _detailOpen: MutableState<DrawerValue> = mutableStateOf(DrawerValue.Closed)
    val detailOpen: State<DrawerValue> get() = _detailOpen

    val smovsState: State<HomeViewModelState> = mutableStateOf(HomeViewModelState())

    val serverUrl: MutableState<String> = mutableStateOf("")

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
            delay(500)
            smovRepository.getSmovPagination(
                pageNum = it,
                pageSize = 500,
                success = { _pageState.value = NetworkState.SUCCESS },
                error = { _pageState.value = NetworkState.ERROR }
            )

        }

    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    init {
        Timber.d("Injection HomeViewModel")

        //Dispatchers.Main：协程在主线程上运行，这个值通常用于更新 UI 或与用户交互。
        //Dispatchers.IO：协程在 I/O 线程池上运行，这个值通常用于执行网络请求、读写文件等 I/O 操作。
        //Dispatchers.Default：协程在默认的线程池上运行，这个值通常用于执行 CPU 密集型的计算任务。
        //Dispatchers.Unconfined：协程在不受限的线程上运行，这个值通常用于测试或调试目的，不应在生产代码中使用。

        //如果在 ViewModel 中执行长时间运行的 IO 操作，最好使用 withContext(Dispatchers.IO) 而不是 viewModelScope.launch(Dispatchers.IO) 来执行这些操作。
        //因为 viewModelScope.launch 返回一个 Job 对象，该对象不会等待执行的操作完成，而是立即返回，因此您需要自己手动控制操作完成后再读取状态。
        //而 withContext 函数可以将 IO 操作包装在一个协程中，并在操作完成后自动返回结果，更加方便。

        viewModelScope.launch(Dispatchers.IO) {
            newSmovFlow.collectLatest {
                smovsState.value.smovs.addAll(it)
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

    fun refreshData() {
        if (smovPageState.value == 0) {
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


