package com.adyen.android.assignment.api.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adyen.android.assignment.api.repository.PlacesRepository

class PlacesViewmodelFactory(private val repository: PlacesRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(PlacesRepository::class.java)
            .newInstance(repository)
    }
}