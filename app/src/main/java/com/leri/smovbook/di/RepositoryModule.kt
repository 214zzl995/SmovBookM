package com.leri.smovbook.di

import android.content.Context
import com.leri.smovbook.datastore.SmovDataStore
import com.leri.smovbook.network.service.SmovService
import com.leri.smovbook.repository.SmovRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Retrofit
import javax.inject.Singleton

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
        smovDataStore:SmovDataStore
    ): SmovRepository {
        return SmovRepository(smovService,smovDataStore)
    }
}