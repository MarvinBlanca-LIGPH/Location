package com.example.location

import androidx.lifecycle.*

class MainViewModelFactory(
    private val activity: MainActivity
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java))
            return MainViewModel(activity) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}