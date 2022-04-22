package com.leri.smovbook.data.smov.impl

import com.example.jetnews.data.Result
import com.google.gson.Gson
import com.leri.smovbook.data.smov.SmovRepository
import com.leri.smovbook.model.Smov
import com.leri.smovbook.model.SmovItem
import okhttp3.*
import java.io.IOException

class SmovRepositoryImpl() : SmovRepository {

    private val client = OkHttpClient();
    private val builder =
        Request.Builder().addHeader("Content-Type", "application/json").get()

    private val gson = Gson();

    override suspend fun getSmovs(serverUrl: String): Result<Smov> {
        var data: Result<Smov> = Result.Error(IllegalArgumentException("未知错误"));
        client.newCall(builder.url(serverUrl).build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                data = Result.Error(IllegalArgumentException("请求服务器错误:" + e.message))
            }

            override fun onResponse(call: Call, response: Response) {
                val res = response.body!!.string()
                val smovList = gson.fromJson(res, Array<SmovItem>::class.java).asList()
                data = Result.Success(
                    Smov(
                        smovList,
                        highlightedSmovItem = smovList.first()
                    )
                )
            }
        })
        return data;
    }
}