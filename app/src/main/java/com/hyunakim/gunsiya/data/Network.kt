package com.hyunakim.gunsiya.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("users")
    suspend fun sendUser(@Body user: User)

    @POST("users")
    suspend fun sendUserWithRecord(@Body userWithRecord: UserWithRecord)

    @POST("records")
    suspend fun sendRecord(@Body record: Record)

    @GET("qna")
    suspend fun getQnas(): List<Qna> // 서버에서 Qna 목록을 가져옵니다.

    @POST("qna")
    suspend fun sendQna(@Body qna: Qna)
}

val retrofit = Retrofit.Builder()
    .baseUrl("https://playfi.site/api/gunsiya/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val apiService = retrofit.create(ApiService::class.java)


class Network {
}