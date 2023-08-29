package com.leri.smovbook.datastore.impl

import android.content.Context
import androidx.datastore.core.DataStore
import com.leri.smovbook.config.Settings
import com.leri.smovbook.config.ThirdPartyPlayer
import com.leri.smovbook.datastore.SettingsDataStore
import com.leri.smovbook.datastore.serializer.settingsDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty

class SettingsDataStoreImpl(context: Context) : SettingsDataStore {
    private val settingsDataStore: DataStore<Settings> = context.settingsDataStore
    override val thirdPartyPlayer: Flow<ThirdPartyPlayer>
        get() =
            settingsDataStore.data.map { settings ->
                settings.thirdPartyPlayer
            }.flowOn(Dispatchers.IO).onEmpty {
                emit(ThirdPartyPlayer.getDefaultInstance())
            }

    override suspend fun setThirdPartyPlayer(thirdPartyPlayer: ThirdPartyPlayer) {
        settingsDataStore.updateData { current ->
            current.toBuilder().setThirdPartyPlayer(thirdPartyPlayer).build()
        }
    }
}