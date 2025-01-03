package com.aptoide.android.aptoidegames.gamegenie.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.GameGenieHistoryEntity

@Dao
interface GameGenieHistoryDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveChatById(gameGenieHistoryEntity: GameGenieHistoryEntity)
}
