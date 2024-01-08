package com.goazzi.skycore.viewmodel

import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import com.goazzi.skycore.model.SearchBusiness
import com.goazzi.skycore.repository.MainRepository

class MainViewModel : ViewModel() {

//    private var _isLocationSwitchEnabled = MutableLiveData<Boolean>(false)
    private var _isLocationSwitchEnabled = MutableState<Boolean>(false)
    val isLocationSwitchEnabled: LiveData<Boolean>
        get() = _isLocationSwitchEnabled

    private val _searchBusiness: MutableLiveData<SearchBusiness> = MutableLiveData()



    val businessServiceClass = _searchBusiness.switchMap {
        MainRepository.searchBusinesses(it.lat, it.lon, it.radius, it.sortBy, it.limit, it.offset)
    }

    /*val businessServiceClass = Transformations.switchMap(_searchBusiness) {
        MainRepository.searchBusinesses(it.lat, it.lon, it.radius, it.sortBy, it.limit, it.offset)
    }*/

    fun setSearchBusiness(searchBusiness: SearchBusiness) {
        _searchBusiness.value = searchBusiness
        /*if (searchBusiness != _searchBusiness.value) {
            _searchBusiness.value = searchBusiness
        }*/
    }

    fun cancelJobs() {
        //cancel pending operations if need be
        MainRepository.cancelJobs()
    }

    companion object {
        private const val TAG = "MainViewModel"
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
