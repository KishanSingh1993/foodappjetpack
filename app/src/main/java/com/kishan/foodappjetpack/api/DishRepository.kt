package com.kishan.foodappjetpack.api
import com.kishan.foodappjetpack.data.Dish
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DishRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://fls8oe8xp7.execute-api.ap-south-1.amazonaws.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(DishApi::class.java)

    suspend fun getDishes(): List<Dish> {
        return api.getDishes()
    }
}