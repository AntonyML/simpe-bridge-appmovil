package com.simpe.bridge.appmovil.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [MessageEntity::class, ReceiptCaptureEntity::class],
    version = 5,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun receiptCaptureDao(): ReceiptCaptureDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE receipt_captures ADD COLUMN visualScore INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE receipt_captures ADD COLUMN semanticScore INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE receipt_captures ADD COLUMN likelihoodScore INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE receipt_captures ADD COLUMN finalScore INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE receipt_captures ADD COLUMN rejectionReasonsJson TEXT NOT NULL DEFAULT '[]'")
                db.execSQL("ALTER TABLE receipt_captures ADD COLUMN ocrSummary TEXT NOT NULL DEFAULT ''")
                db.execSQL("UPDATE receipt_captures SET visualScore = score, finalScore = score WHERE visualScore = 0")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "messages.db",
                )
                .addMigrations(MIGRATION_4_5)
                .fallbackToDestructiveMigration()
                .build().also { INSTANCE = it }
            }
        }
    }
}
