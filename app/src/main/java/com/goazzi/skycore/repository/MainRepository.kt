package com.goazzi.skycore.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.goazzi.skycore.model.BusinessesServiceClass
import com.goazzi.skycore.repository.api.RetrofitBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

object MainRepository {

    private const val TAG = "MainRepository"
    var job: CompletableJob? = null

    fun searchBusinesses(
        lat: Double,
        lon: Double,
        radius: Int,
        sortBy: String,
        limit: Int,
        offset: Int
    ): LiveData<BusinessesServiceClass> {
        job = Job()
        return object : LiveData<BusinessesServiceClass>() {
            override fun onActive() {
                super.onActive()
                job?.let {
                    CoroutineScope(IO + it).launch {
                        /*val business: BusinessesServiceClass =
                            RetrofitBuilder.apiService.searchBusinesses(
                                lat,
                                lon,
                                radius,
                                sortBy,
                                limit,
                                offset
                            )*/

                        val resBody = RetrofitBuilder.apiService.searchBusinessesBody(
                            lat,
                            lon,
                            radius,
                            sortBy,
                            limit,
                            offset
                        )
                        val body = resBody.body().apply {
                            this?.code = resBody.code()
                            this?.message = resBody.message()
                        }

                        /*withContext(Main){
                            value = business
                            it.complete()
                        }*/
//                        postValue(resBody.body())
                        postValue(body)
                        it.complete()
                    }
                }
            }
        }
    }

    fun cancelJobs() {
        job?.cancel()
    }
}