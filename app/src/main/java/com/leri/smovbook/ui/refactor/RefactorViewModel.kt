package com.leri.smovbook.ui.refactor

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leri.smovbook.repository.SmovRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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

    private val moviePageStateFlow: MutableStateFlow<Int> = MutableStateFlow(1)

    private val _smovStateFlow: MutableState<Int> = mutableStateOf(-1)

    val smovStateFlow: State<Int> get() = _smovStateFlow

    val smovFlow = smovSharedFlow.flatMapLatest {
        smovRepository.getSmovAll()
    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)


    private val stateTestFlow = moviePageStateFlow.flatMapLatest {
        smovRepository.getSmovAll()
    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)


    init {
        Timber.d("Injection RefactorViewModel")
        viewModelScope.launch(Dispatchers.IO) {
            stateTestFlow.collectLatest {
                _smovStateFlow.value = _smovStateFlow.value + 1
            }
        }
    }

    fun fetchPersonDetailsById(id: Long) = smovSharedFlow.tryEmit(id)

    fun changeUrlTest(url: String) = smovRepository.changeSmovServiceUrl(url)

    fun fetchNextMoviePage() {
        moviePageStateFlow.value++
    }


}
