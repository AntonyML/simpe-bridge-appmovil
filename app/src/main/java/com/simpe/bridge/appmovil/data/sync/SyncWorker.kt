package com.simpe.bridge.appmovil.data.sync

import android.content.Context
import androidx.work.*
import com.simpe.bridge.appmovil.data.auth.SessionManager
import com.simpe.bridge.appmovil.data.local.AppDatabase
import com.simpe.bridge.appmovil.data.local.toDomain
import com.simpe.bridge.appmovil.data.remote.NetworkResult
import com.simpe.bridge.appmovil.data.remote.SinpeBridgeHttpManager
import com.simpe.bridge.appmovil.domain.usecases.MessageStatus
import java.util.concurrent.TimeUnit

class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val dao = AppDatabase.getInstance(applicationContext).messageDao()
        val pending = dao.getUnsynced()
        if (pending.isEmpty()) return Result.success()

        val httpClient = SinpeBridgeHttpManager.getClient()
        var allSuccess = true

        pending.forEach { message ->
            // Note: This automatic sync won't have the correlationToken from the UI.
            // For now, it will use an empty token. 
            // The manual "Test SMS" flow will handle the token correctly as requested.
            val result = httpClient.postSmsMessage(
                message = message.toDomain(),
                correlationToken = ""
            )

            if (result is NetworkResult.Success) {
                dao.updateStatus(message.messageId, MessageStatus.SENT, System.currentTimeMillis())
            } else {
                allSuccess = false
                android.util.Log.e("SyncWorker", "Fallo envío de mensaje ${message.messageId}")
            }
        }

        return if (allSuccess) Result.success() else Result.retry()
    }

    companion object {
        private const val WORK_NAME = "simpe_message_sync"

        /** Sync periódico cada 15 min mientras haya conexión */
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        /** Trigger inmediato — se llama al guardar cada mensaje nuevo */
        fun triggerOnce(context: Context) {
            val request = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
            WorkManager.getInstance(context).enqueue(request)
        }
    }
}
