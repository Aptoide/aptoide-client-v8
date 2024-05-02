package com.aptoide.android.aptoidegames.installer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aptoide.android.aptoidegames.installer.database.model.AppDetailsData
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDetailsDao {

  @Query("SELECT * from AppDetails")
  suspend fun getAll(): List<AppDetailsData>

  @Query("SELECT * from AppDetails WHERE packageName = :packageName")
  suspend fun getByPackage(packageName: String): AppDetailsData?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun save(AppDetailsData: AppDetailsData)

  @Query("DELETE FROM AppDetails WHERE packageName = :packageName")
  suspend fun remove(packageName: String)

  @Query("SELECT * from AppDetails WHERE packageName = :packageName")
  fun getByPackageAsFlow(packageName: String): Flow<AppDetailsData>
}
