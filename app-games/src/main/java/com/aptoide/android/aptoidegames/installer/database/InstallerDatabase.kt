package com.aptoide.android.aptoidegames.installer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aptoide.android.aptoidegames.installer.database.model.AppDetailsData

@Database(
  version = 1,
  entities = [AppDetailsData::class]
)

abstract class InstallerDatabase : RoomDatabase() {
  abstract fun appInfoDao(): AppDetailsDao

}
