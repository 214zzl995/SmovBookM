package com.leri.smovbook.config

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.leri.smovbook.Application
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.InputStream
import java.io.OutputStream

/**
 * @Description: DataStore序列化测试
 * @author DingWei
 * @version 10/8/2022 下午7:13
 */
object SettingsSerializer : Serializer<Settings> {
    override val defaultValue: Settings = Settings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Settings {
        try {
            return Settings.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: Settings,
        output: OutputStream
    ) = t.writeTo(output)
}

val Context.settingsDataStore: DataStore<Settings> by dataStore(
    fileName = "settings.pb",
    serializer = SettingsSerializer
)

val settingsDataStore: DataStore<Settings> = Application.getContext().settingsDataStore
suspend fun incrementCounter() {
    settingsDataStore.updateData { currentSettings ->
        currentSettings.toBuilder()
            .setExampleCounter(currentSettings.exampleCounter + 1)
            .build()
    }
}

val exampleCounterFlow: Flow<Int> = settingsDataStore.data
    .map { settings ->
        // The exampleCounter property is generated from the proto schema.
        settings.exampleCounter
    }

