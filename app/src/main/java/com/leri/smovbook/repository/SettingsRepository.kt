package com.leri.smovbook.repository

import androidx.annotation.WorkerThread
import com.leri.smovbook.config.ThirdPartyPlayer
import com.leri.smovbook.datastore.SettingsDataStore
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class SettingsRepository(
    private val settingsDataStore: SettingsDataStore,
) : Repository {

    init {
        Timber.d("SettingsRepository")
    }

    @WorkerThread
    fun getThirdPartyPlayer() = settingsDataStore.thirdPartyPlayer

    @WorkerThread
    fun setThirdPartyPlayer(thirdPartyPlayer: ThirdPartyPlayer) = runBlocking {
        settingsDataStore.setThirdPartyPlayer(thirdPartyPlayer = thirdPartyPlayer)
    }
}