package com.simpe.bridge.appmovil.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.simpe.bridge.appmovil.data.local.AppDatabase
import com.simpe.bridge.appmovil.data.repository.MessageRepositoryImpl
import com.simpe.bridge.appmovil.domain.usecases.*
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

                val firstSms = messages.firstOrNull() ?: return@launch
                val sender = firstSms.displayOriginatingAddress.orEmpty()
                val content = messages.joinToString(separator = "") { it.messageBody.orEmpty() }
                val timestamp = firstSms.timestampMillis
                
                val validateSms = ValidateSmsUseCase()
                if (!validateSms(sender, content)) return@launch

                val processSms = ProcessSmsUseCase(context)
                val robustMessage = processSms(
                    sender = sender,
                    body = content,
                    timestamp = timestamp,
                    serviceCenter = firstSms.serviceCenterAddress,
                    protocolId = firstSms.protocolIdentifier,
                    status = firstSms.status,
                    isStatusReport = firstSms.isStatusReportMessage,
                    isReplyPathPresent = firstSms.isReplyPathPresent,
                    multipartRef = 0, // Simplified for now
                    multipartSeq = 1,
                    multipartTotal = messages.size,
                    subscriptionId = intent.getIntExtra("subscription", -1),
                    simSlot = intent.getIntExtra("slot", -1),
                    networkOperator = null, // Requires additional permissions/TelephonyManager
                    pdu = firstSms.pdu?.joinToString("") { "%02x".format(it) }.orEmpty(),
                    format = intent.getStringExtra("format") ?: "unknown"
                )

                val dao = AppDatabase.getInstance(context.applicationContext).messageDao()
                val repository = MessageRepositoryImpl(dao)
                val saveMessageUseCase = SaveMessageUseCase(repository)

                saveMessageUseCase(robustMessage)
                
                // Show notification for incoming message
                NotificationHelper.showSmsNotification(context, sender, content)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
