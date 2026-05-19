package com.simpe.bridge.appmovil.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptCaptureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(capture: ReceiptCaptureEntity)

    @Query("SELECT * FROM receipt_captures ORDER BY createdAt DESC")
    fun getAll(): Flow<List<ReceiptCaptureEntity>>

    @Query("UPDATE receipt_captures SET uploaded = 1, uploadMessage = :message WHERE captureId = :captureId")
    suspend fun markUploaded(captureId: String, message: String)

    @Query("SELECT EXISTS(SELECT 1 FROM receipt_captures WHERE sha256 = :hash AND uploaded = 1 LIMIT 1)")
    suspend fun hasUploadedHash(hash: String): Boolean
}
