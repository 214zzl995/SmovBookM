package com.leri.smovbook.datastore

import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton
import com.leri.smovbook.config.ThirdPartyPlayer


@Singleton
interface SettingsDataStore {

    val  thirdPartyPlayer: Flow<ThirdPartyPlayer>

}