package com.leri.smovbook.data.smov.impl

import com.example.jetnews.data.Result
import com.google.gson.Gson
import com.leri.smovbook.data.smov.SmovRepository
import com.leri.smovbook.model.ServerResult
import com.leri.smovbook.model.Smov
import com.leri.smovbook.model.SmovItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException

//是否需要在以后将请求改造为 retrofit2 https://stackoverflow.com/questions/32559333/retrofit-2-dynamic-url
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
                    delay(1000)
                    var smovList = listOf<SmovItem>()
                    //这里直接转化为这个类型 当我请求出错时 会出现问题
                    val serverResult = gson.fromJson(res, ServerResult::class.java)
                    if (serverResult.code == 200) {
                        try {
                            smovList = serverResult.data
                            //gson.fromJson(serverResult.data, Array<SmovItem>::class.java).asList()
                        } catch (e: Exception) {
                            Result.Error(IllegalArgumentException("出现错误:" + e.message))

                        }
                        Result.Success(
                            Smov(
                                smovList,
                                highlightedSmovItem = smovList.first()
                            )
                        )
                    }else{
                        Result.Error(IllegalArgumentException("错误:" + serverResult.msg))
                    }
                } catch (e: Exception) {
                    Result.Error(IllegalArgumentException("出现错误:" + e.message))
                }
            }
        }

    }

}



