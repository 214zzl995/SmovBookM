package com.leri.smovbook.repository

import androidx.annotation.WorkerThread
import com.leri.smovbook.datastore.SettingsDataStore
import timber.log.Timber

class SettingsRepository(
    private val settingsDataStore: SettingsDataStore,
) : Repository {

    init {
        Timber.d("SettingsRepository")
    }

    @WorkerThread
    fun getThirdPartyPlayer() = settingsDataStore.thirdPartyPlayer


}