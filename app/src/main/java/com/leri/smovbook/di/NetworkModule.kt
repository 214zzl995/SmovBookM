package com.leri.smovbook.di

import coil.util.CoilUtils
import com.leri.smovbook.datastore.SmovDataStore
import com.leri.smovbook.network.Api
import com.leri.smovbook.network.RequestInterceptor
import com.leri.smovbook.network.ignoreAllSSLErrors
import com.leri.smovbook.network.service.SmovService
import com.skydoves.sandwich.adapters.ApiResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(smovDataStore: SmovDataStore): OkHttpClient {
        //需要实现完整的HTTP/2 需要实现 ALPN
        return OkHttpClient.Builder()
            .protocols(listOf(Protocol.H2_PRIOR_KNOWLEDGE))
            .addInterceptor(RequestInterceptor(smovDataStore))
            .ignoreAllSSLErrors()
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okhHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okhHttpClient)
            .baseUrl(Api.BASE_URL)
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideTheSmovService(retrofit: Retrofit ): SmovService {
        return retrofit.create(SmovService::class.java)
    }


}
