package com.skylock.ai_cartoon.enhance

// FILE: enhancer/EnhancerViewModelFactory.kt


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EnhancerViewModelFactory(private val feature: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EnhancerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EnhancerViewModel(feature) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}