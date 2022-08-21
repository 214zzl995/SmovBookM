package com.leri.smovbook.ui.home

import androidx.lifecycle.ViewModel
import com.leri.smovbook.repository.SmovRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel1 @Inject constructor(
    private val smovRepository: SmovRepository
) : ViewModel() {



}