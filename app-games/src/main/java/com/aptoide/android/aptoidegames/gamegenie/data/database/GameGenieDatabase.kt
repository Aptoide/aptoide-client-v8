package com.aptoide.android.aptoidegames.gamegenie.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.Converters
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.GameGenieHistoryEntity

@Database(entities = [GameGenieHistoryEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class GameGenieDatabase : RoomDatabase() {

  abstract fun getGameGenieHistoryDao(): GameGenieHistoryDao
}
