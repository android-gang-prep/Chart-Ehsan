package com.example.chart

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


class Network {
    companion object{
        lateinit var instance:Retrofit

        fun setup(){
            instance = Retrofit.Builder()
                .baseUrl("https://www.binance.com/api/v3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}

interface NetworkApi{
    @GET("uiKlines?limit=1000")
    suspend fun getCandles(@Query("symbol") crypto:String,@Query("interval") interval:String = "4h"):Response<List<List<String>>>
}

