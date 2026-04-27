package com.simpe.bridge.appmovil.data.local

import androidx.room.TypeConverter
import com.simpe.bridge.appmovil.domain.usecases.MessageStatus

class Converters {
    @TypeConverter
    fun fromStatus(status: MessageStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): MessageStatus = MessageStatus.valueOf(value)
}
