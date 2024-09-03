package com.aptoide.android.aptoidegames.installer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aptoide.android.aptoidegames.installer.database.model.AppDetailsData

@Database(
  version = 2,
  entities = [AppDetailsData::class]
)

abstract class InstallerDatabase : RoomDatabase() {
  abstract fun appInfoDao(): AppDetailsDao

  class FirstMigration : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
      database.execSQL("ALTER TABLE AppDetails ADD COLUMN appId INTEGER")
    }
  }
}
