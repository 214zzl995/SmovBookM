package com.leri.smovbook.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jetnews.data.Result
import com.leri.smovbook.R
import com.leri.smovbook.data.smov.SmovRepository
import com.leri.smovbook.model.Smov
import com.leri.smovbook.model.SmovItem
import com.leri.smovbook.util.DataStoreUtils
import com.leri.smovbook.util.ErrorMessage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

sealed interface HomeUiState {

    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>
    val searchInput: String
    val serverUrl: String

    data class NoData(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String,
        override val serverUrl: String
    ) : HomeUiState

    data class HasData(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String,
        override val serverUrl: String,
        val smovs: Smov,
        val selectedSmov: SmovItem,
        val isDetailOpen: Boolean
    ) : HomeUiState
}

private data class HomeViewModelState(
    val smovs: Smov? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
    val searchInput: String = "",
    val serverUrl: String = "",
    val selectedSmovId: Int = 0,
    val isDetailOpen: Boolean = false
) {
    fun toUiState(): HomeUiState =
        if (smovs == null) {
            HomeUiState.NoData(
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput,
                serverUrl = serverUrl
            )
        } else {
            HomeUiState.HasData(
                smovs = smovs,
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput,
                serverUrl = serverUrl,
                isDetailOpen = isDetailOpen,
                selectedSmov = smovs.smovList.find {
                    it.id == selectedSmovId
                } ?: smovs.highlightedSmovItem,
            )
        }
}


class HomeViewModel(
    private val smovRepository: SmovRepository
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
        viewModelState.update {
            it.copy(serverUrl = DataStoreUtils.getData("server_url", ""))
        }
    }

    private fun refreshData() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = smovRepository.getSmovs(viewModelState.value.serverUrl)
            viewModelState.update {
                when (result) {
                    is Result.Success -> it.copy(smovs = result.data, isLoading = false)
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
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(smovRepository) as T
            }
        }
    }
}