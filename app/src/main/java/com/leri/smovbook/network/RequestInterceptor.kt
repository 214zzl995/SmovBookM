package com.leri.smovbook.network

import com.leri.smovbook.datastore.SmovDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response

internal class RequestInterceptor(private val smovDataStore: SmovDataStore) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()

        //获取根url
        val baseUrl = runBlocking {
            smovDataStore.serverUrl.first()
        }

        //获取根端口
        val port = runBlocking {
            smovDataStore.getServerPort().first()
        }

        val originalUrl = originalRequest.url

        val url = if (baseUrl.isNotBlank()) {
            originalUrl
                .newBuilder()
                .scheme("http")
                .host(baseUrl)
                .port(port).build()
        } else {
            originalUrl.newBuilder().build()
        }

        println("拦截器url$url")

        //动态设置url
        val requestBuilder = originalRequest.newBuilder().url(url)
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}
