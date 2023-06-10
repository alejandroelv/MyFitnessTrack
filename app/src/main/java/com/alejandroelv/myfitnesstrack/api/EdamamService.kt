package com.alejandroelv.myfitnesstrack.api

import com.alejandroelv.myfitnesstrack.data.model.edamamModels.Foods
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface EdamamService {

    @GET("parser")
    fun getFoods(
        @Query("app_id") appId: String,
        @Query("app_key") clave: String,
        @Query("ingr") foodName: String
    ): Call<Foods>
}