package com.hyunakim.gunsiya.data

import android.content.Context

interface AppContainer {
    val usersRepository: UsersRepository
    val recordsRepository : RecordsRepository
    val qnasRepository : QnasRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val usersRepository: UsersRepository by lazy {
        OfflineUsersRepository(GunsiyaDatabase.getDatabase(context).userDao())
    }
    override val recordsRepository: RecordsRepository by lazy {
        OfflineRecordsRepository(GunsiyaDatabase.getDatabase(context).recordDao())
    }
    override val qnasRepository: QnasRepository by lazy {
        OfflineQnasRepository(GunsiyaDatabase.getDatabase(context).qnaDao())
    }
}