package com.leri.smovbook.ui.refactor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leri.smovbook.repository.SmovRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import timber.log.Timber
import javax.inject.Inject

/**
 * @Description: XXX
 * @author DingWei
 * @version 12/8/2022 下午3:53
 */
@HiltViewModel
class RefactorViewModel @Inject constructor(
    private val smovRepository: SmovRepository
) : ViewModel() {

    private val smovSharedFlow: MutableSharedFlow<Long> = MutableSharedFlow(replay = 1)

    val personFlow = smovSharedFlow.flatMapLatest {
        val s = smovRepository.getSmovTest()
        println(s)
        s
    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    init {
        Timber.d("Injection RefactorViewModel")
    }

    fun fetchPersonDetailsById(id: Long) = smovSharedFlow.tryEmit(id)


}