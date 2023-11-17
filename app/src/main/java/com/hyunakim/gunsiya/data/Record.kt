package com.hyunakim.gunsiya.data

import androidx.room.Entity
import androidx.room.PrimaryKey

//@Entity(tableName = "records")
//data class Record(
//    @PrimaryKey(autoGenerate = true) val id: Int,
//    val userId: Int, // 이 값은 User 테이블의 id와 연관됩니다.
//    val date: String, // YYYY-MM-DD 형식을 가정합니다.
//    val isAtropineDrop: String,
//    val item2: String,
//    val item3: String
//)

@Entity(tableName = "records", primaryKeys = ["userId", "date"])
data class Record(
    val userId: Int, // 이 값은 User 테이블의 id와 연관됩니다.
    val date: String, // YYYY-MM-DD 형식을 가정합니다.
    val isAtropineDrop: Boolean,
    val timeOutdoorActivity: Int,
    val timeCloseWork: Int
)