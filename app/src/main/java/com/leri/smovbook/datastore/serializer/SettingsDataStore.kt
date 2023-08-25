package com.leri.smovbook.datastore.serializer

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.leri.smovbook.config.Settings

val Context.settingsDataStore: DataStore<Settings> by dataStore(
    fileName = "settings.pb",
    serializer = SettingsSerializer
)