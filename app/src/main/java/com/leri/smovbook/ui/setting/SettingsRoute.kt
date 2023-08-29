package com.leri.smovbook.ui.setting

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.leri.smovbook.config.ThirdPartyPlayer
import com.leri.smovbook.viewModel.SettingsViewModel

@Composable
fun SettingsRoute(
    navigateUp: () -> Unit,
    settingsViewModel: SettingsViewModel,
) {

    val thirdPartyPlayer  by settingsViewModel.thirdPartyPlayer

    SettingsScreen(
        thirdPartyPlayer = thirdPartyPlayer,
        setThirdPartyPlayer = {
            settingsViewModel.setThirdPartyPlayer(it)
        }
    )

}