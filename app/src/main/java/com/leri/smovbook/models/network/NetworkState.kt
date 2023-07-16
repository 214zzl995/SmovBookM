package com.leri.smovbook.models.network

import androidx.compose.runtime.Composable

enum class NetworkState {
    IDLE,
    LOADING,
    ERROR,
    SUCCESS
}

@Composable
fun NetworkState.onSuccess(block: @Composable () -> Unit): NetworkState {
    if (this == NetworkState.SUCCESS) {
        block()
    }
    return this
}

@Composable
fun NetworkState.onLoading(block: @Composable () -> Unit): NetworkState {
    if (this == NetworkState.LOADING) {
        block()
    }
    return this
}

@Composable
fun NetworkState.isLoading(): Boolean {
    return this == NetworkState.LOADING
}

@Composable
fun NetworkState.isError(): Boolean {
    return this == NetworkState.ERROR
}

@Composable
fun NetworkState.isSuccess(): Boolean {
    return this == NetworkState.SUCCESS
}

