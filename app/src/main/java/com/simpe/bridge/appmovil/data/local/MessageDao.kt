package com.simpe.bridge.appmovil.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.simpe.bridge.appmovil.domain.usecases.MessageStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(message: MessageEntity)

    @Query("SELECT * FROM messages ORDER BY createdAt DESC")
    fun getAll(): Flow<List<MessageEntity>>

    @Query("UPDATE messages SET status = :status, lastAttemptAt = :lastAttemptAt, retryCount = retryCount + 1 WHERE messageId = :id")
    suspend fun updateStatus(id: String, status: MessageStatus, lastAttemptAt: Long)
}
