package com.leri.smovbook.di

import com.leri.smovbook.datastore.SmovDataStore
import com.leri.smovbook.network.service.SmovService
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
    fun provideTvRepository(
        smovService: SmovService,
        smovDataStore: SmovDataStore,
        okhHttpClient: OkHttpClient
    ): SmovRepository {
        return SmovRepository(smovService, smovDataStore,okhHttpClient.dispatcher)
    }
}