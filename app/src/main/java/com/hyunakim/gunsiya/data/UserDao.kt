package com.hyunakim.gunsiya.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User) // Create

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUser(userId: Int): Flow<User> // Read

    @Update
    suspend fun update(user: User) // Update

    @Delete
    suspend fun delete(user: User) // Delete

    // 여러 사용자를 한 번에 가져오는 메소드도 추가할 수 있습니다.
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>
}
