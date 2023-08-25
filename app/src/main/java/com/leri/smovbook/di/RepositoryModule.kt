package com.leri.smovbook.di

import com.leri.smovbook.datastore.ServicesDataStore
import com.leri.smovbook.datastore.SettingsDataStore
import com.leri.smovbook.network.service.SmovService
import com.leri.smovbook.repository.ServicesRepository
import com.leri.smovbook.repository.SettingsRepository
import com.leri.smovbook.repository.SmovRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import okhttp3.OkHttpClient

/**
 * @Description: XXX
 * @author DingWei
 * @version 12/8/2022 下午3:02
 */
@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    @ViewModelScoped
    fun provideSmovRepository(
        smovService: SmovService,
    ): SmovRepository {
        return SmovRepository(smovService)
    }

    @Provides
    @ViewModelScoped
    fun provideServiceRepository(
        servicesDataStore: ServicesDataStore,
        okhHttpClient: OkHttpClient,
    ): ServicesRepository {
        return ServicesRepository(servicesDataStore, okhHttpClient.dispatcher)
    }

    @Provides
    @ViewModelScoped
    fun provideSettingsRepository(
        settingsDataStore: SettingsDataStore,
        okhHttpClient: OkHttpClient,
    ): SettingsRepository {
        return SettingsRepository(settingsDataStore)
    }


}