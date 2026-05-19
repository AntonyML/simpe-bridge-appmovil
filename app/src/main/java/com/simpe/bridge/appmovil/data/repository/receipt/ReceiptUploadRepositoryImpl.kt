package com.simpe.bridge.appmovil.data.repository.receipt

import android.content.Context
import com.google.gson.Gson
import com.simpe.bridge.appmovil.BuildConfig
import com.simpe.bridge.appmovil.domain.receipt.ReceiptUploadRepository
import com.simpe.bridge.appmovil.domain.receipt.ReceiptUploadRequest
import com.simpe.bridge.appmovil.domain.receipt.ReceiptUploadResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.UUID
import java.util.concurrent.TimeUnit

class ReceiptUploadRepositoryImpl(
    context: Context,
    private val endpoint: String = BuildConfig.RECEIPT_UPLOAD_URL,
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build(),
    private val gson: Gson = Gson(),
) : ReceiptUploadRepository {

    private val sentHashes = context.getSharedPreferences("receipt_upload_hashes", Context.MODE_PRIVATE)

    override suspend fun upload(request: ReceiptUploadRequest): ReceiptUploadResult = withContext(Dispatchers.IO) {
        if (endpoint.isBlank()) {
            error("RECEIPT_UPLOAD_URL no esta configurado")
        }

        val hash = request.optimizedImage.sha256
        if (sentHashes.getBoolean(hash, false)) {
            return@withContext ReceiptUploadResult(
                remoteId = hash,
                duplicate = true,
                message = "Comprobante duplicado detectado localmente",
            )
        }

        val imageFile = File(requireNotNull(request.optimizedImage.imageUri.path))
        val metadataJson = gson.toJson(request.optimizedImage.metadata)
        val uploadId = UUID.randomUUID().toString()

        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                name = "image",
                filename = "image.webp",
                body = imageFile.asRequestBody("image/webp".toMediaType()),
            )
            .addFormDataPart("hash", hash)
            .addFormDataPart("device_id", request.deviceId)
            .addFormDataPart("timestamp", request.timestamp.toString())
            .addFormDataPart("metadata", metadataJson)
            .addFormDataPart("app_version", request.appVersion)
            .addFormDataPart("upload_id", uploadId)
            .build()

        val httpRequest = Request.Builder()
            .url(endpoint)
            .post(body)
            .header("Accept", "application/json")
            .header("X-Receipt-Hash", hash)
            .build()

        client.newCall(httpRequest).execute().use { response ->
            if (!response.isSuccessful) {
                error("Error HTTP ${response.code}: ${response.body?.string().orEmpty()}")
            }
            sentHashes.edit().putBoolean(hash, true).apply()
            ReceiptUploadResult(
                remoteId = response.header("X-Receipt-Id") ?: uploadId,
                duplicate = false,
                message = "Comprobante enviado",
            )
        }
    }
}
