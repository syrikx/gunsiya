package com.hyunakim.gunsiya

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hyunakim.gunsiya.ui.MainViewModel


class MainViewModelFactory(
    private val alarmManagerHelper: AlarmManagerHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(alarmManagerHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

