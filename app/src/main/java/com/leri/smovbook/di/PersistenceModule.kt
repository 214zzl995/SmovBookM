package com.leri.smovbook.di


import android.content.Context
import com.leri.smovbook.datastore.SmovDataStore
import com.leri.smovbook.datastore.impl.SmovDataStoreImpl
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
    fun provideTheSmovDataStore(@ApplicationContext context: Context): SmovDataStore {
        return SmovDataStoreImpl(context)
    }


}
