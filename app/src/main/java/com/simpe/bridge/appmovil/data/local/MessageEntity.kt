package com.simpe.bridge.appmovil.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.simpe.bridge.appmovil.domain.usecases.MessageStatus

@Entity(
    tableName = "messages",
    indices = [Index(value = ["contentHash"], unique = true)]
)
data class MessageEntity(
    @PrimaryKey
    val messageId: String,
    val correlationId: String,
    val createdAt: Long,
    val source: String,
    val version: String,
    val contentHash: String,
    val deviceHash: String,
    val signature: String,
    val status: MessageStatus,
    val retryCount: Int,
    val lastAttemptAt: Long?,
    
    // Payload fields
    val sender: String,
    val body: String,
    val timestamp: Long,
    
    // Metadata
    val serviceCenter: String?,
    val protocolId: Int,
    val smsStatus: Int,
    val isStatusReport: Boolean,
    val isReplyPathPresent: Boolean,
    
    // Multipart
    val multipartRef: Int,
    val multipartSeq: Int,
    val multipartTotal: Int,
    
    // Device
    val subscriptionId: Int,
    val simSlot: Int,
    val networkOperator: String?,
    
    // Debug
    val pdu: String,
    val format: String
)
