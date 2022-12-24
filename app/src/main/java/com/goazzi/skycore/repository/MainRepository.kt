package com.goazzi.skycore.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.goazzi.skycore.model.Business
import com.goazzi.skycore.model.BusinessesServiceClass
import com.goazzi.skycore.model.Todos
import com.goazzi.skycore.repository.api.RetrofitBuilder

import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object MainRepository {

    private const val TAG = "MainRepository"
    var job: CompletableJob? = null

    fun searchBusinesses(
        lat: Double,
        lon: Double,
        radius: Int,
        sortBy: String,
        limit: Int
    ): LiveData<BusinessesServiceClass> {
        job = Job()
        return object : LiveData<BusinessesServiceClass>() {
            override fun onActive() {
                super.onActive()
                job?.let {
                    CoroutineScope(IO + it).launch {
                        val business: BusinessesServiceClass =
                            RetrofitBuilder.apiService.searchBusinesses(
                                lat,
                                lon,
                                radius,
                                sortBy,
                                limit
                            )
                        /*withContext(Main){
                            value = business
                            it.complete()
                        }*/
                        postValue(business)
                        it.complete()
                    }
                }
            }
        }
    }

    suspend fun insert(todos: Todos) {
//        todosDao.insert(todos)
    }

    fun cancelJobs() {
        job?.cancel()
    }
}