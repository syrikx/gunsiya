package com.hyunakim.gunsiya.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class, Record::class], version = 2)
abstract class GunsiyaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun recordDao(): RecordDao

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