package com.aptoide.android.aptoidegames.gamegenie.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.Converters
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.GameGenieHistoryEntity

@Database(entities = [GameGenieHistoryEntity::class], version = 2)
@TypeConverters(Converters::class)
abstract class GameGenieDatabase : RoomDatabase() {

  abstract fun getGameGenieHistoryDao(): GameGenieHistoryDao

  class FirstMigration : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
      db.execSQL("ALTER TABLE GameGenieHistory ADD COLUMN title TEXT")
    }
  }
}
