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

/**
 * SmsReceiver handles incoming SMS messages and manages their lifecycle state machine.
 * 
 * FLOW:
 * 1. Capture SMS -> Initial State: PENDING (Memory only)
 * 2. Validate SMS -> If invalid -> Final State: FAILED (Persisted)
 * 3. Process SMS -> If error -> Final State: FAILED (Persisted)
 * 4. Persist SMS -> Final State: SENT (Persisted)
 */
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
                // 1. Capture SMS data
                val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                if (messages.isEmpty()) {
                    pendingResult.finish()
                    return@launch
                }

                val firstSms = messages.firstOrNull() ?: throw IllegalStateException("Empty message list")
                val sender = firstSms.displayOriginatingAddress.orEmpty()
                val content = messages.joinToString(separator = "") { it.messageBody.orEmpty() }
                val timestamp = firstSms.timestampMillis

                // Shared repository and DAO
                val dao = AppDatabase.getInstance(context.applicationContext).messageDao()
                val repository = MessageRepositoryImpl(dao)
                val saveMessageUseCase = SaveMessageUseCase(repository)
                val processSms = ProcessSmsUseCase(context)
                val validateSms = ValidateSmsUseCase()

                // State determination
                var finalStatus = MessageStatus.SENT

                // 2. Validate SMS
                if (!validateSms(sender, content)) {
                    finalStatus = MessageStatus.FAILED
                }

                try {
                    // 3. Process SMS (Generates IDs, hashes, signatures)
                    // If validation failed above, we still process it but with FAILED status for traceability
                    val robustMessage = processSms(
                        sender = sender,
                        body = content,
                        timestamp = timestamp,
                        serviceCenter = firstSms.serviceCenterAddress,
                        protocolId = firstSms.protocolIdentifier,
                        smsStatus = firstSms.status,
                        isStatusReport = firstSms.isStatusReportMessage,
                        isReplyPathPresent = firstSms.isReplyPathPresent,
                        multipartRef = 0,
                        multipartSeq = 1,
                        multipartTotal = messages.size,
                        subscriptionId = intent.getIntExtra("subscription", -1),
                        simSlot = intent.getIntExtra("slot", -1),
                        networkOperator = null,
                        pdu = firstSms.pdu?.joinToString("") { "%02x".format(it) }.orEmpty(),
                        format = intent.getStringExtra("format") ?: "unknown",
                        messageStatus = finalStatus
                    )

                    // 4. Final Persistence
                    saveMessageUseCase(robustMessage)
                    
                    // Trigger notification only on success
                    if (finalStatus == MessageStatus.SENT) {
                        NotificationHelper.showSmsNotification(context, sender, content)
                    }

                } catch (processingException: Exception) {
                    // Fail-safe persistence for unexpected processing errors
                    val failedMessage = processSms(
                        sender = sender,
                        body = content,
                        timestamp = timestamp,
                        serviceCenter = firstSms.serviceCenterAddress,
                        protocolId = firstSms.protocolIdentifier,
                        smsStatus = firstSms.status,
                        isStatusReport = firstSms.isStatusReportMessage,
                        isReplyPathPresent = firstSms.isReplyPathPresent,
                        multipartRef = 0,
                        multipartSeq = 1,
                        multipartTotal = messages.size,
                        subscriptionId = intent.getIntExtra("subscription", -1),
                        simSlot = intent.getIntExtra("slot", -1),
                        networkOperator = null,
                        pdu = firstSms.pdu?.joinToString("") { "%02x".format(it) }.orEmpty(),
                        format = intent.getStringExtra("format") ?: "unknown",
                        messageStatus = MessageStatus.FAILED
                    )
                    saveMessageUseCase(failedMessage)
                    processingException.printStackTrace()
                }

            } catch (e: Exception) {
                // Critical system failure
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
