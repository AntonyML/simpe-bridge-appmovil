package com.simpe.bridge.appmovil.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.simpe.bridge.appmovil.data.local.AppDatabase
import com.simpe.bridge.appmovil.data.repository.MessageRepositoryImpl
import com.simpe.bridge.appmovil.domain.usecases.SaveMessageUseCase
import com.simpe.bridge.appmovil.domain.usecases.SmsMessage
import com.simpe.bridge.appmovil.notifications.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION &&
            intent.action != Telephony.Sms.Intents.SMS_DELIVER_ACTION
        ) {
            return
        }

        val pendingResult = goAsync()

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                if (messages.isEmpty()) return@launch

                val sender = messages.firstOrNull()?.displayOriginatingAddress.orEmpty()
                val content = messages.joinToString(separator = "") { it.messageBody.orEmpty() }
                val timestamp = messages.firstOrNull()?.timestampMillis ?: System.currentTimeMillis()

                val dao = AppDatabase.getInstance(context.applicationContext).messageDao()
                val repository = MessageRepositoryImpl(dao)
                val saveMessageUseCase = SaveMessageUseCase(repository)

                saveMessageUseCase(
                    SmsMessage(
                        sender = sender,
                        content = content,
                        timestamp = timestamp,
                    )
                )
                
                // Show notification for incoming message
                NotificationHelper.showSmsNotification(context, sender, content)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
