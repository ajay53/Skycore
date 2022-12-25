package com.goazzi.skycore.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.goazzi.skycore.model.SearchBusiness
import com.goazzi.skycore.repository.MainRepository

class MainViewModel : ViewModel() {

    private val _searchBusiness: MutableLiveData<SearchBusiness> = MutableLiveData()

    val businessServiceClass = Transformations.switchMap(_searchBusiness) {
        MainRepository.searchBusinesses(it.lat, it.lon, it.radius, it.sortBy, it.limit, it.offset)
    }

    fun setSearchBusiness(searchBusiness: SearchBusiness) {
        if (searchBusiness != _searchBusiness.value) {
            _searchBusiness.value = searchBusiness
        }
    }

    fun cancelJobs() {
        MainRepository.cancelJobs()
    }
}

class MainViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
