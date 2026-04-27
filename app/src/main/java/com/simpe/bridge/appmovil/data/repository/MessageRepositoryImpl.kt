package com.simpe.bridge.appmovil.data.repository

import com.simpe.bridge.appmovil.data.local.MessageDao
import com.simpe.bridge.appmovil.data.local.toDomain
import com.simpe.bridge.appmovil.data.local.toEntity
import com.simpe.bridge.appmovil.domain.usecases.MessageRepository
import com.simpe.bridge.appmovil.domain.usecases.SmsMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MessageRepositoryImpl(
    private val dao: MessageDao,
) : MessageRepository {

    override fun getMessages(): Flow<List<SmsMessage>> {
        return dao.getAll().map { items -> items.map { it.toDomain() } }
    }

    override suspend fun saveMessage(message: SmsMessage) {
        dao.insert(message.toEntity())
    }
}
