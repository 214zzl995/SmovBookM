package com.leri.smovbook.datastore.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.leri.smovbook.config.Services
import com.leri.smovbook.config.Settings
import java.io.InputStream
import java.io.OutputStream

/**
 * @Description: DataStore序列化测试
 * @author DingWei
 * @version 10/8/2022 下午7:13
 */
object ServicesSerializer : Serializer<Services> {
    override val defaultValue: Services = Services.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Services {
        try {
            return Services.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: Services, output: OutputStream) = t.writeTo(output)

}

object SettingsSerializer : Serializer<Settings> {
    override val defaultValue: Settings = Settings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Settings {
        try {
            return Settings.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: Settings, output: OutputStream) = t.writeTo(output)

}






