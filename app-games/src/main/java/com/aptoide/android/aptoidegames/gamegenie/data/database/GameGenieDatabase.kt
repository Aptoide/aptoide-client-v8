package com.aptoide.android.aptoidegames.gamegenie.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.Converters
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.GameCompanionEntity
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.GameGenieHistoryEntity

@Database(entities = [GameGenieHistoryEntity::class, GameCompanionEntity::class], version = 6)
@TypeConverters(Converters::class)
abstract class GameGenieDatabase : RoomDatabase() {

  abstract fun getGameGenieHistoryDao(): GameGenieHistoryDao

  abstract fun getGameCompanionDao(): GameCompanionDao

  class FirstMigration : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
      db.execSQL("ALTER TABLE GameGenieHistory ADD COLUMN title TEXT")
    }
  }

  class SecondMigration : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
      db.execSQL("DROP TABLE GameGenieHistory")
      db.execSQL(
        "CREATE TABLE GameGenieHistory (id TEXT NOT NULL PRIMARY KEY, conversation TEXT NOT NULL, title TEXT NOT NULL DEFAULT '')"
      )
    }
  }

  class ThirdMigration : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
      db.execSQL(
        """
      CREATE TABLE IF NOT EXISTS `GameCompanion` (
        `id` TEXT NOT NULL PRIMARY KEY,
        `name` TEXT NOT NULL,
        `conversation` TEXT NOT NULL,
        `gamePackageName` TEXT NOT NULL
      )
      """.trimIndent()
      )
    }
  }

  class FourthMigration : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
      db.execSQL(
        """
          ALTER TABLE GameCompanion 
            ADD COLUMN lastMessageTimestamp INTEGER NOT NULL DEFAULT 0
      """.trimIndent()
      )
    }
  }
}
