package com.leri.smovbook.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.leri.smovbook.data.Result
import com.leri.smovbook.R
import com.leri.smovbook.config.SettingsRepository
import com.leri.smovbook.data.smov.SmovRepository
import com.leri.smovbook.model.Smov
import com.leri.smovbook.model.SmovItem
import com.leri.smovbook.util.DataStoreUtils
import com.leri.smovbook.util.ErrorMessage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

sealed interface HomeUiState {

    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>
    val searchInput: String
    val serverUrl: String
    val historyUrl: Set<String>

    data class NoData(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String,
        override val serverUrl: String,
        override val historyUrl: Set<String>
    ) : HomeUiState

    data class HasData(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String,
        override val serverUrl: String,
        override val historyUrl: Set<String>,
        val smov: Smov,
        val selectedSmov: SmovItem,
        val isDetailOpen: Boolean
    ) : HomeUiState
}

private data class HomeViewModelState(
    val smov: Smov? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
    val searchInput: String = "",
    val serverUrl: String = "",
    val selectedSmovId: Int = 0,
    val isDetailOpen: Boolean = false,
    val historyUrl: Set<String> = setOf()
) {
    fun toUiState(): HomeUiState =
        if (smov == null) {
            HomeUiState.NoData(
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput,
                serverUrl = serverUrl,
                historyUrl = historyUrl
            )
        } else {
            HomeUiState.HasData(
                smov = smov,
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput,
                serverUrl = serverUrl,
                isDetailOpen = isDetailOpen,
                selectedSmov = smov.smovList.find {
                    it.id == selectedSmovId
                } ?: smov.highlightedSmovItem,
                historyUrl = historyUrl
            )
        }
}


class HomeViewModel(
    private val smovRepository: SmovRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val viewModelState = MutableStateFlow(HomeViewModelState(isLoading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        getCacheServe()
        refreshData()
    }

    private fun getCacheServe() {

        //这个viewModelScope.launch是协程 是不阻塞当前线程的
        viewModelScope.launch {

        }

        //而runBlocking是阻塞的 会等到取到url再进行下一步
        runBlocking {
            viewModelState.update {
                val serverUrl = DataStoreUtils.getData("server_url", "")
                it.copy(serverUrl = serverUrl)
            }
        }


    }

    fun changeCacheServe(url: String) {

        //这个viewModelScope.launch是协程 是不阻塞当前线程的
        viewModelScope.launch {
            println("测试data" + settingsRepository.getServerUrl())
            settingsRepository.changeServerUrl(url)
        }

        viewModelState.update {
            it.copy(serverUrl = url)
        }
        DataStoreUtils.putData("server_url", url)
        refreshData()
    }

    fun refreshData() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = smovRepository.getSmovs(viewModelState.value.serverUrl)
            println(result)
            viewModelState.update {
                when (result) {
                    is Result.Success -> it.copy(smov = result.data, isLoading = false)
                    is Result.Error -> {
                        val errorMessages = it.errorMessages + ErrorMessage(
                            id = UUID.randomUUID().mostSignificantBits,
                            messageId = R.string.load_error
                        )
                        it.copy(errorMessages = errorMessages, isLoading = false)
                    }
                }
            }
        }
    }

    fun getServer(): String {
        return viewModelState.value.serverUrl.ifBlank {
            "127.0.0.1"
        }
    }

    fun errorShown(errorId: Long) {
        viewModelState.update { currentUiState ->
            val errorMessages = currentUiState.errorMessages.filterNot { it.id == errorId }
            currentUiState.copy(errorMessages = errorMessages)
        }
    }

    fun onSearchInputChanged(searchInput: String) {
        viewModelState.update {
            it.copy(searchInput = searchInput)
        }
    }

    companion object {
        fun provideFactory(
            smovRepository: SmovRepository,
            settingsRepository: SettingsRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(smovRepository, settingsRepository) as T
            }
        }
    }
}