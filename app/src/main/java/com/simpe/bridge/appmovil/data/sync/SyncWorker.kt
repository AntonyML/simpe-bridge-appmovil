package com.simpe.bridge.appmovil.data.sync

import android.content.Context
import androidx.work.*
import com.simpe.bridge.appmovil.data.auth.SessionManager
import com.simpe.bridge.appmovil.data.local.AppDatabase
import com.simpe.bridge.appmovil.data.remote.SupabaseMessageService
import com.simpe.bridge.appmovil.domain.usecases.MessageStatus
import java.util.concurrent.TimeUnit

class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val sessionManager = SessionManager(applicationContext)
        if (!sessionManager.isLoggedIn()) return Result.success() // sin sesión: no hay nada que sincronizar

        val dao     = AppDatabase.getInstance(applicationContext).messageDao()
        val pending = dao.getUnsynced()
        if (pending.isEmpty()) return Result.success()

        val service = SupabaseMessageService(sessionManager)
        return service.syncMessages(pending).fold(
            onSuccess = {
                val now = System.currentTimeMillis()
                pending.forEach { dao.updateStatus(it.messageId, MessageStatus.SENT, now) }
                Result.success()
            },
            onFailure = {
                android.util.Log.e("SyncWorker", "Sync fallido: ${it.message}")
                Result.retry() // WorkManager reintenta con backoff exponencial
            }
        )
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
