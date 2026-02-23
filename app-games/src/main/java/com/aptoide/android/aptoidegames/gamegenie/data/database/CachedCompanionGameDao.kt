package com.aptoide.android.aptoidegames.gamegenie.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.CachedCompanionGameEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CachedCompanionGameDao {

  @Query("SELECT * FROM CachedCompanionGame")
  abstract fun getAllCached(): Flow<List<CachedCompanionGameEntity>>

  @Query("SELECT * FROM CachedCompanionGame")
  abstract suspend fun getAllCachedOnce(): List<CachedCompanionGameEntity>

  @Query("SELECT COALESCE(MAX(cachedAtMs), 0) FROM CachedCompanionGame")
  abstract suspend fun getLatestTimestamp(): Long

  @Transaction
  open suspend fun replaceAll(games: List<CachedCompanionGameEntity>) {
    if (games.isEmpty()) return
    clearAll()
    insertAll(games)
  }

  @Query("DELETE FROM CachedCompanionGame")
  abstract suspend fun clearAll()

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract suspend fun insertAll(games: List<CachedCompanionGameEntity>)
}
