package com.hyunakim.gunsiya

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hyunakim.gunsiya.data.AppContainer
import com.hyunakim.gunsiya.data.AppDataContainer
import com.hyunakim.gunsiya.data.OfflineUsersRepository
import com.hyunakim.gunsiya.data.User
import com.hyunakim.gunsiya.data.UserDao
import com.hyunakim.gunsiya.data.UsersRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//@Database(entities = [User::class], version = 1)
//@TypeConverters(LocalDateTimeConverter::class)
//abstract class AppDatabase : RoomDatabase() {
//    abstract fun userDao(): UserDao
//}
//
//class AppDataContainer(context: Context) : AppContainer {
//    private val appDatabase: AppDatabase = Room.databaseBuilder(
//        context.applicationContext,
//        AppDatabase::class.java, "gunsiya-database"
//    ).fallbackToDestructiveMigration().build()
//
//    private val userDao: UserDao = appDatabase.userDao()
//
//    override fun provideUsersRepository(): UsersRepository {
//        return OfflineUsersRepository(userDao)
//    }
//
//    // 기타 필요한 메서드들...
//}

class GunsiyaApplication : Application() {

    lateinit var container: AppContainer

    companion object {
        lateinit var sharedPreferences: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences("GunsiyaAppPrefs", Context.MODE_PRIVATE)
        container = AppDataContainer(this)
        val usersRepository = container.usersRepository
        UserManager.initializeWithMostRecentUser(usersRepository)
    }

    object UserManager {
        private val applicationScope = CoroutineScope(Dispatchers.Default)
        private val _currentUser = MutableStateFlow<User?>(null)
        val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

        fun initializeWithMostRecentUser(usersRepository: UsersRepository) {
            applicationScope.launch {
                usersRepository.getAllUsersStream().collect { users ->
                    val mostRecentUser = users.maxByOrNull { it.lastSelectedTime }
                    mostRecentUser?.let { setCurrentUser(it) }
                }
            }
        }

        fun setCurrentUser(user: User) {
            _currentUser.value = user
        }
    }
}
