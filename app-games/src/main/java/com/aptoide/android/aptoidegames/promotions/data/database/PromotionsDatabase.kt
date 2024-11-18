package com.aptoide.android.aptoidegames.promotions.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SkippedPromotionEntity::class], version = 1)
abstract class PromotionsDatabase : RoomDatabase() {

  abstract fun getSkippedPromotionsDao(): SkippedPromotionsDao
}
