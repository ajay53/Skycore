package com.goazzi.skycore.repository.api

import com.goazzi.skycore.model.BusinessesServiceClass
import com.goazzi.skycore.model.Todos
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("search")
    suspend fun searchBusinesses(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("radius") radius: Int,
        @Query("sort_by") sortBy: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): BusinessesServiceClass

    @GET("search")
    suspend fun searchBusinessesBody(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("radius") radius: Int,
        @Query("sort_by") sortBy: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Response<BusinessesServiceClass>

    @GET("todos/{id}")
    suspend fun getTodosBody(@Path("id") id: Int): Response<Todos>
}