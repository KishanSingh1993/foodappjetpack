package com.kishan.foodappjetpack.api

import com.kishan.foodappjetpack.data.Dish
import retrofit2.http.GET

interface DishApi {
    @GET("dev/nosh-assignment")
    suspend fun getDishes(): List<Dish>
}