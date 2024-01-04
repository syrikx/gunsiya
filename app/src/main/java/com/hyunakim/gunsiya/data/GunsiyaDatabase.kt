package com.hyunakim.gunsiya.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.datetime.LocalDateTime

@Database(entities = [User::class, Record::class, Qna::class], version = 12)
@TypeConverters(Converters::class)
abstract class GunsiyaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun recordDao(): RecordDao
    abstract fun qnaDao(): QnaDao

    companion object {
//        private var INSTANCE : GunsiyaDatabase? = null
//
//        fun getInstance(context : Context): GunsiyaDatabase {
//            if (INSTANCE == null) {
//                INSTANCE =
//                    Room.databaseBuilder(context.applicationContext, GunsiyaDatabase::class.java,"app_database").build()
//            }
//            return INSTANCE!!
//        }
        @Volatile
        private var Instance : GunsiyaDatabase? = null
        fun getDatabase(context: Context):GunsiyaDatabase{
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, GunsiyaDatabase::class.java, "gunsiya_database").fallbackToDestructiveMigration().build().also { Instance=it }
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.toString()
    }
}

