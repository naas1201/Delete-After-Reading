package com.example.notificationthriller.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room database for the application with AAA-level choice tracking
 */
@Database(
    entities = [
        Message::class,
        GameState::class,
        ChoiceConsequence::class,
        CharacterRelationship::class,
        StoryState::class,
        Achievement::class,
        PlayerStats::class,
    ],
    version = 3,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao

    abstract fun gameStateDao(): GameStateDao

    abstract fun choiceConsequenceDao(): ChoiceConsequenceDao

    abstract fun characterRelationshipDao(): CharacterRelationshipDao

    abstract fun storyStateDao(): StoryStateDao

    abstract fun achievementDao(): AchievementDao

    abstract fun playerStatsDao(): PlayerStatsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 =
            object : Migration(1, 2) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    // Add new columns to messages table
                    db.execSQL("ALTER TABLE messages ADD COLUMN choices TEXT")
                    db.execSQL("ALTER TABLE messages ADD COLUMN parentChoiceId INTEGER")
                    db.execSQL("ALTER TABLE messages ADD COLUMN isChoiceBranch INTEGER NOT NULL DEFAULT 0")

                    // Create game_states table
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS game_states (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            saveName TEXT NOT NULL,
                            saveTimestamp INTEGER NOT NULL,
                            currentMessageId INTEGER NOT NULL,
                            userChoices TEXT NOT NULL,
                            completedMessages TEXT NOT NULL
                        )
                        """.trimIndent(),
                    )
                }
            }

        private val MIGRATION_2_3 =
            object : Migration(2, 3) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    // Create choice_consequences table
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS choice_consequences (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            messageId INTEGER NOT NULL,
                            choiceId INTEGER NOT NULL,
                            characterAffected TEXT NOT NULL,
                            relationshipDelta INTEGER NOT NULL,
                            trustDelta INTEGER NOT NULL,
                            storyBranchUnlocked TEXT,
                            achievementUnlocked TEXT,
                            timestamp INTEGER NOT NULL,
                            wasRemembered INTEGER NOT NULL DEFAULT 0
                        )
                        """.trimIndent(),
                    )

                    // Create character_relationships table
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS character_relationships (
                            characterName TEXT PRIMARY KEY NOT NULL,
                            trustLevel INTEGER NOT NULL DEFAULT 50,
                            relationshipStatus TEXT NOT NULL DEFAULT 'Neutral',
                            keychoicesMade TEXT NOT NULL,
                            isAlive INTEGER NOT NULL DEFAULT 1,
                            lastInteractionTime INTEGER NOT NULL DEFAULT 0
                        )
                        """.trimIndent(),
                    )

                    // Create story_state table
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS story_state (
                            id INTEGER PRIMARY KEY NOT NULL DEFAULT 1,
                            currentChapter INTEGER NOT NULL DEFAULT 1,
                            storyPath TEXT NOT NULL DEFAULT 'Main',
                            moralityScore INTEGER NOT NULL DEFAULT 50,
                            cautionScore INTEGER NOT NULL DEFAULT 50,
                            conspiracyDepth INTEGER NOT NULL DEFAULT 0,
                            majorChoicesMade TEXT NOT NULL,
                            unlockedEndings TEXT NOT NULL,
                            playthrough INTEGER NOT NULL DEFAULT 1,
                            lastSaveTimestamp INTEGER NOT NULL DEFAULT 0
                        )
                        """.trimIndent(),
                    )

                    // Create achievements table
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS achievements (
                            achievementId TEXT PRIMARY KEY NOT NULL,
                            title TEXT NOT NULL,
                            description TEXT NOT NULL,
                            isUnlocked INTEGER NOT NULL DEFAULT 0,
                            unlockedTimestamp INTEGER NOT NULL DEFAULT 0,
                            isHidden INTEGER NOT NULL DEFAULT 0,
                            rarity TEXT NOT NULL DEFAULT 'Common'
                        )
                        """.trimIndent(),
                    )

                    // Create player_stats table
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS player_stats (
                            id INTEGER PRIMARY KEY NOT NULL DEFAULT 1,
                            totalChoicesMade INTEGER NOT NULL DEFAULT 0,
                            totalPlaytime INTEGER NOT NULL DEFAULT 0,
                            messagesRead INTEGER NOT NULL DEFAULT 0,
                            completedPlaythroughs INTEGER NOT NULL DEFAULT 0,
                            fastestCompletion INTEGER NOT NULL DEFAULT 0,
                            mostTrustedCharacter TEXT,
                            favoriteEnding TEXT,
                            totalAchievements INTEGER NOT NULL DEFAULT 0,
                            secretsFound INTEGER NOT NULL DEFAULT 0,
                            totalDeaths INTEGER NOT NULL DEFAULT 0
                        )
                        """.trimIndent(),
                    )
                }
            }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "notification_thriller_database",
                    )
                        .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                        // Note: fallbackToDestructiveMigration() removed for production to preserve user data
                        // Only use during development if needed
                        .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
