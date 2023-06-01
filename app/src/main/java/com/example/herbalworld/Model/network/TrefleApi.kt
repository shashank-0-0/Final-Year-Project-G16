package com.example.herbalworld.Model.network

import com.example.herbalworld.Model.network.model.Herbs
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface TrefleApi {

    @GET("api/v1/plants")
    suspend fun getHerbs(
        @Query("filter[medical]") medical:Boolean,
        @Query("token") key: String,
        @Query("page") page: Int,
    ): Herbs
}