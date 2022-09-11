package com.leri.smovbook.ui.smovDetail

import androidx.lifecycle.ViewModel
import com.leri.smovbook.repository.SmovRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SmovDetailViewModel @Inject constructor(
    private val smovRepository: SmovRepository
): ViewModel() {

}