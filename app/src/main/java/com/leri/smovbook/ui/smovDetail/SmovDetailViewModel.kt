package com.leri.smovbook.ui.smovDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leri.smovbook.repository.SmovRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

@HiltViewModel
class SmovDetailViewModel @Inject constructor(
    private val smovRepository: SmovRepository
) : ViewModel() {

    private val smovIdSharedFlow: MutableSharedFlow<Long> = MutableSharedFlow(replay = 1)

    val smovFlow = smovIdSharedFlow.flatMapLatest {
        smovRepository.getSmovById(it,
            success = {},
            error = {}
        )
    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    fun fetchSmovDetailsById(id: Long) = smovIdSharedFlow.tryEmit(id)
}