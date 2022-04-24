package com.leri.smovbook.data.smov.impl

import com.example.jetnews.data.Result
import com.google.gson.Gson
import com.leri.smovbook.data.smov.SmovRepository
import com.leri.smovbook.model.Smov
import com.leri.smovbook.model.SmovItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import okhttp3.*
import java.io.IOException
import kotlinx.coroutines.withContext

//是否需要在以后将请求改造为 retrofit2
class SmovRepositoryImpl : SmovRepository {

    override suspend fun getSmovsAsync(serverUrl: String): Result<Smov> {
        val client = OkHttpClient();
        val builder =
            Request.Builder().addHeader("Content-Type", "application/json").get()
        val gson = Gson();

        var data: Result<Smov> = Result.Error(IllegalArgumentException("未知错误"));
        val url = "http://$serverUrl/data"
        println(url)
        if (serverUrl.isBlank()) {
            data = Result.Error(IllegalArgumentException("未配置服务器地址"));
        } else {
            client.newCall(builder.url(url).build()).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    data = Result.Error(IllegalArgumentException("请求服务器错误:" + e.message))
                }

                override fun onResponse(call: Call, response: Response) {
                    val res = response.body!!.string()
                    try {
                        val smovList = gson.fromJson(res, Array<SmovItem>::class.java).asList()

                        data = Result.Success(
                            Smov(
                                smovList,
                                highlightedSmovItem = smovList.first()
                            )
                        )
                    } catch (e: Exception) {
                        data = Result.Error(IllegalArgumentException("格式化返回数据出现错误:" + e.message))
                    }
                }
            })
        }
        return data;
    }

    override suspend fun getSmovs(serverUrl: String): Result<Smov> {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient();
            val builder =
                Request.Builder().addHeader("Content-Type", "application/json").get()
            val gson = Gson();
            val url = "http://$serverUrl/data"
            if (serverUrl.isBlank()) {
                Result.Error(IllegalArgumentException("未配置服务器地址"));
            } else {
                try {
                    val result = client.newCall(builder.url(url).build()).execute()
                    val res = result.body!!.string()
                    delay(2000)
                    val smovList = gson.fromJson(res, Array<SmovItem>::class.java).asList()
                    Result.Success(
                        Smov(
                            smovList,
                            highlightedSmovItem = smovList.first()
                        )
                    )
                } catch (e: Exception) {
                    Result.Error(IllegalArgumentException("出现错误:" + e.message))
                }
            }
        }

    }

}



