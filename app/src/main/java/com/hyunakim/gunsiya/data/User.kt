package com.hyunakim.gunsiya.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val birthDate: String, // YYYY-MM-DD 형식을 가정합니다.
    val hospitalCode: String,
    val patientCode: String, // 변경된 변수명입니다.
    val isCurrentUser : Boolean
)


