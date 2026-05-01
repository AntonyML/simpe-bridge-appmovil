package com.simpe.bridge.appmovil.data.remote

import com.simpe.bridge.appmovil.data.auth.SessionManager
import com.simpe.bridge.appmovil.data.auth.supabaseClient
import com.simpe.bridge.appmovil.data.local.MessageEntity
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class SupabaseMessageService(private val sessionManager: SessionManager) {

    /**
     * Sincroniza mensajes PENDING/FAILED a Supabase.
     * Usa upsert en message_id → idempotente, sin duplicados.
     */
    suspend fun syncMessages(messages: List<MessageEntity>): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val deviceUUID = sessionManager.getOrCreateDeviceUUID()
                val records    = messages.map { it.toMessageRecord(deviceUUID) }
                supabaseClient.from("messages").upsert(records) {
                    onConflict = "message_id"
                }
                Unit
            }
        }
}

// ---------------------------------------------------------------------------
// Data class @Serializable — mapeada 1:1 con la tabla Supabase
// Campos: exactamente los de MessageEntity + device_uuid
// ---------------------------------------------------------------------------
@Serializable
data class MessageRecord(
    @SerialName("message_id")            val messageId: String,
    @SerialName("correlation_id")        val correlationId: String,
    @SerialName("created_at_epoch")      val createdAtEpoch: Long,
    @SerialName("source")                val source: String,
    @SerialName("version")               val version: String,
    @SerialName("content_hash")          val contentHash: String,
    @SerialName("device_hash")           val deviceHash: String,
    @SerialName("device_uuid")           val deviceUuid: String,
    @SerialName("signature")             val signature: String,
    @SerialName("status")                val status: String,
    @SerialName("retry_count")           val retryCount: Int,
    @SerialName("last_attempt_at")       val lastAttemptAt: Long?,
    @SerialName("sender")                val sender: String,
    @SerialName("body")                  val body: String,
    @SerialName("timestamp_epoch")       val timestampEpoch: Long,
    @SerialName("service_center")        val serviceCenter: String?,
    @SerialName("protocol_id")           val protocolId: Int,
    @SerialName("sms_status")            val smsStatus: Int,
    @SerialName("is_status_report")      val isStatusReport: Boolean,
    @SerialName("is_reply_path_present") val isReplyPathPresent: Boolean,
    @SerialName("multipart_ref")         val multipartRef: Int,
    @SerialName("multipart_seq")         val multipartSeq: Int,
    @SerialName("multipart_total")       val multipartTotal: Int,
    @SerialName("subscription_id")       val subscriptionId: Int,
    @SerialName("sim_slot")              val simSlot: Int,
    @SerialName("network_operator")      val networkOperator: String?,
    @SerialName("pdu")                   val pdu: String,
    @SerialName("format")                val format: String,
    @SerialName("classification")        val classification: String,
    @SerialName("detection_confidence")  val detectionConfidence: Float,
    @SerialName("detection_details")     val detectionDetails: String
)

fun MessageEntity.toMessageRecord(deviceUUID: String) = MessageRecord(
    messageId           = messageId,
    correlationId       = correlationId,
    createdAtEpoch      = createdAt,
    source              = source,
    version             = version,
    contentHash         = contentHash,
    deviceHash          = deviceHash,
    deviceUuid          = deviceUUID,
    signature           = signature,
    status              = status.name,
    retryCount          = retryCount,
    lastAttemptAt       = lastAttemptAt,
    sender              = sender,
    body                = body,
    timestampEpoch      = timestamp,
    serviceCenter       = serviceCenter,
    protocolId          = protocolId,
    smsStatus           = smsStatus,
    isStatusReport      = isStatusReport,
    isReplyPathPresent  = isReplyPathPresent,
    multipartRef        = multipartRef,
    multipartSeq        = multipartSeq,
    multipartTotal      = multipartTotal,
    subscriptionId      = subscriptionId,
    simSlot             = simSlot,
    networkOperator     = networkOperator,
    pdu                 = pdu,
    format              = format,
    classification      = classification,
    detectionConfidence = detectionConfidence,
    detectionDetails    = detectionDetails
)
