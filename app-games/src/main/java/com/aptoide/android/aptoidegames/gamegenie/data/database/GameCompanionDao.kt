package com.aptoide.android.aptoidegames.gamegenie.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.GameCompanionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameCompanionDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveChat(gameGenieHistoryEntity: GameCompanionEntity)

  @Query("SELECT * FROM GameCompanion WHERE gamePackageName=:packageName")
  suspend fun getChatByPackageName(packageName: String): GameCompanionEntity

  @Query("SELECT * FROM GameCompanion ORDER BY lastMessageTimestamp DESC")
  fun getAllChats(): Flow<List<GameCompanionEntity>>
}
