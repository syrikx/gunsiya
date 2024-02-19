package com.hyunakim.gunsiya.data

import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("users")
    suspend fun sendUser(@Body user: User)

    @POST("users")
    suspend fun sendUserWithRecord(@Body userWithRecord: UserWithRecord): ApiResponse

    @POST("records")
    suspend fun sendRecord(@Body record: Record)

    @GET("qna")
    suspend fun getQnasByPatient(@Query("patient") patientId: String): List<QnaServer>

    @POST("qna")
    suspend fun sendQna(@Body qna: Qna): PostQnaResponse
}

data class ApiResponse(val success: Boolean, val message: String)

//data class QnaResponse(
//    val status: String,
//    val totalResults: Int,
//    val articles: List<Qna>
//)

data class QnaServer(
    @PrimaryKey
    var qnaId: String, // 서버에서 String 타입으로 정의되었습니다.
    val patient: String, // 환자를 식별하는 데 사용될 수 있는 String 타입의 필드
    val question: String,
    val answer: String,
    val isAnswered: Boolean = false, // 기본값을 false로 설정
    val dateCreated: String?, // 생성 날짜
    val dateAnswered: String? // 답변 날짜, 답변이 없을 수 있으므로 nullable 타입
)

data class PostQnaResponse(
    val success: Boolean,
    val qnaId: String
)

val retrofit = Retrofit.Builder()
    .baseUrl("https://playfi.site/api/gunsiya/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val apiService = retrofit.create(ApiService::class.java)


class Network {
}