package com.leri.smovbook.datastore.serializer

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.leri.smovbook.config.Services

val Context.servicesDataStore: DataStore<Services> by dataStore(
    fileName = "services.pb",
    serializer = ServicesSerializer
)

