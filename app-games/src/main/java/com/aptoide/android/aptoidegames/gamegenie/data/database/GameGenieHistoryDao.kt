package com.aptoide.android.aptoidegames.gamegenie.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.GameGenieHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameGenieHistoryDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveChat(gameGenieHistoryEntity: GameGenieHistoryEntity)

  @Query("SELECT * FROM GameGenieHistory")
  fun getAllChats(): Flow<List<GameGenieHistoryEntity>>

  @Query("SELECT * FROM GameGenieHistory WHERE id=:chatId")
  suspend fun getChatById(chatId: String): GameGenieHistoryEntity

  @Query("DELETE FROM GameGenieHistory WHERE id=:chatId")
  suspend fun deleteChat(chatId: String)
}
