package com.leri.smovbook.network

import android.annotation.SuppressLint
import com.leri.smovbook.datastore.SmovDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

internal class RequestInterceptor(private val smovDataStore: SmovDataStore) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()

        //获取根url
        val baseUrl = runBlocking {
            smovDataStore.serverUrl.first()
        }

        //获取根端口
        val port = runBlocking {
            smovDataStore.serverPort.first()
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

        println("正在请求url：$url")

        //动态设置url
        val requestBuilder = originalRequest.newBuilder().url(url).tag("request")
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

fun OkHttpClient.Builder.ignoreAllSSLErrors(): OkHttpClient.Builder {
    val naiveTrustManager = @SuppressLint("CustomX509TrustManager")
    object : X509TrustManager {
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) = Unit
        override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) = Unit
    }

    val insecureSocketFactory = SSLContext.getInstance("TLSv1.2").apply {
        val trustAllCerts = arrayOf<TrustManager>(naiveTrustManager)
        init(null, trustAllCerts, SecureRandom())
    }.socketFactory

    sslSocketFactory(insecureSocketFactory, naiveTrustManager)
    hostnameVerifier { _, _ -> true }
    return this
}





