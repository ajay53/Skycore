package com.goazzi.skycore.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.goazzi.skycore.model.Business
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
    ): LiveData<Business> {
        job = Job()
        return object : LiveData<Business>() {
            override fun onActive() {
                super.onActive()
                job?.let {
                    CoroutineScope(IO + it).launch {
//                        val todos: Todos = RetrofitBuilder.fakeApiService.getTodos(id)
                        /*val business: Business = RetrofitBuilder.apiService.searchBusinesses(
                            lat,
                            lon,
                            radius,
                            sortBy,
                            limit
                        )*/
                        val resBody = RetrofitBuilder.apiService.searchBusinessesBody(
                            lat,
                            lon,
                            radius,
                            sortBy,
                            limit
                        )
                        resBody.enqueue(object : Callback<ResponseBody> {
                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                Log.d(TAG, "onResponse: ${response.body()?.string()}")
                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Log.d(TAG, "onFailure: ${t.stackTrace}")
                            }

                        })

//                        resBody.body()
//                        resBody.code()
//                        resBody.message()
                        /*withContext(Main){
                            value = todos
                            it.complete()
                        }*/
//                        postValue(business)
//                        postValue(todos)
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