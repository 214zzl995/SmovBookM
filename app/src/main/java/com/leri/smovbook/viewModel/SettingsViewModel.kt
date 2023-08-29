package com.leri.smovbook.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leri.smovbook.config.ThirdPartyPlayer
import com.leri.smovbook.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _thirdPartyPlayer: MutableState<ThirdPartyPlayer> =
        mutableStateOf(ThirdPartyPlayer.getDefaultInstance())

    init {
        Timber.d("Injection SettingsVIewModel")

        viewModelScope.launch(Dispatchers.Main) {
            settingsRepository.getThirdPartyPlayer()
                .collectLatest {
                    _thirdPartyPlayer.value = it
                }
        }
    }

    fun setThirdPartyPlayer(thirdPartyPlayer: ThirdPartyPlayer) {
        settingsRepository.setThirdPartyPlayer(thirdPartyPlayer)
    }


    val thirdPartyPlayer: State<ThirdPartyPlayer> get() = _thirdPartyPlayer


}