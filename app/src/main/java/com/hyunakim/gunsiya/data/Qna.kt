package com.hyunakim.gunsiya.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.datetime.LocalDateTime
import java.util.Date

@Entity(tableName = "qnas")
//data class Qna(
//    @SerializedName("_id") val id: String,
//    val isAnswered: Boolean,
//    val answer: String,
//    val patient: String,
//    val question: String,
//    @SerializedName("__v") val version: Int,
//    val date: QnaDate
//)
//
//data class QnaDate(
//    val created: Date,
//    val answered: Date
//)
data class Qna(
    @PrimaryKey
    var qnaId: String, // 서버에서 String 타입으로 정의되었습니다.
    val backendQnaId: String, // 서버에서 String 타입으로 정의되었습니다.
    val patient: String, // 환자를 식별하는 데 사용될 수 있는 String 타입의 필드
    val question: String,
    val answer: String,
    val isAnswered: Boolean = false, // 기본값을 false로 설정
    val dateCreated: LocalDateTime?, // 생성 날짜
    val dateAnswered: LocalDateTime? // 답변 날짜, 답변이 없을 수 있으므로 nullable 타입
)
