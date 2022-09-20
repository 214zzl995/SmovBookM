package com.leri.smovbook.ui.player

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject


@HiltViewModel
class SmovBookVideoActiveViewModel @Inject constructor(
    val application: Application
) : ViewModel() {

    private val videoUrl: MutableSharedFlow<String> = MutableSharedFlow(replay = 1)
}