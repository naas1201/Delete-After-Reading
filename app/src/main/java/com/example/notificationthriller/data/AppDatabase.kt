package com.example.notificationthriller.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room database for the application
 */
@Database(
    entities = [Message::class, GameState::class], 
    version = 2, 
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun messageDao(): MessageDao
    abstract fun gameStateDao(): GameStateDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new columns to messages table
                database.execSQL("ALTER TABLE messages ADD COLUMN choices TEXT")
                database.execSQL("ALTER TABLE messages ADD COLUMN parentChoiceId INTEGER")
                database.execSQL("ALTER TABLE messages ADD COLUMN isChoiceBranch INTEGER NOT NULL DEFAULT 0")
                
                // Create game_states table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS game_states (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        saveName TEXT NOT NULL,
                        saveTimestamp INTEGER NOT NULL,
                        currentMessageId INTEGER NOT NULL,
                        userChoices TEXT NOT NULL,
                        completedMessages TEXT NOT NULL
                    )
                """.trimIndent())
            }
        }
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notification_thriller_database"
                )
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
