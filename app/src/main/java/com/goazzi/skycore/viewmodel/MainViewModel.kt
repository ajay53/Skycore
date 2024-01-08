package com.goazzi.skycore.viewmodel

import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import com.goazzi.skycore.model.SearchBusiness
import com.goazzi.skycore.repository.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {

    private var _isLocationSwitchEnabled: MutableState<Boolean> = mutableStateOf(false)
    val isLocationSwitchEnabled: State<Boolean>
        get() = _isLocationSwitchEnabled

    fun setLocationSwitch(isEnabled: Boolean) {
        _isLocationSwitchEnabled.value = isEnabled
        _radius.value = 100f
    }
    //    private var _isLocationSwitchEnabled = MutableLiveData<Boolean>(false)

    /*val isLocationSwitchEnabled: LiveData<Boolean>
        get() = _isLocationSwitchEnabled*/

    private var _radius: MutableFloatState = mutableFloatStateOf(100f)
    val radius: State<Float>
        get() = _radius

    fun setRadius(radius: Float) {
        _radius.floatValue = radius
    }

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
