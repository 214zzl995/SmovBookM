package com.leri.smovbook.di


import android.content.Context
import com.leri.smovbook.datastore.ServicesDataStore
import com.leri.smovbook.datastore.SettingsDataStore
import com.leri.smovbook.datastore.impl.ServicesDataStoreImpl
import com.leri.smovbook.datastore.impl.SettingsDataStoreImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

    @Provides
    @Singleton
    fun provideTheServicesDataStore(@ApplicationContext context: Context): ServicesDataStore {
        return ServicesDataStoreImpl(context)
    }

    @Provides
    @Singleton
    fun provideTheSettingsDataStore(@ApplicationContext context: Context): SettingsDataStore {
        return SettingsDataStoreImpl(context)
    }


}
