package com.aptoide.android.aptoidegames.promotions.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface SkippedPromotionsDao : SkippedPromotionsRepository {

  @Transaction
  override suspend fun getSkippedPromotions(): List<String> {
    deleteOutdatedSkippedPromotions()
    return getSavedSkippedPromotions().map { it.packageName }
  }

  override suspend fun skipPromotion(packageName: String) {
    insertSkippedPromotion(
      SkippedPromotionEntity(
        packageName = packageName,
        skippedAt = System.currentTimeMillis()
      )
    )
  }

  @Query("SELECT * from SkippedPromotionEntities")
  suspend fun getSavedSkippedPromotions(): List<SkippedPromotionEntity>

  @Query("DELETE FROM SkippedPromotionEntities WHERE skippedAt < strftime('%s', 'now', '-1 month') * 1000")
  suspend fun deleteOutdatedSkippedPromotions()

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSkippedPromotion(skippedPromotion: SkippedPromotionEntity)
}
