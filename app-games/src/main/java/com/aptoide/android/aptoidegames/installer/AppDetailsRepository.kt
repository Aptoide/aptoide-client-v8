package com.aptoide.android.aptoidegames.installer

import com.aptoide.android.aptoidegames.installer.database.AppDetailsDao
import com.aptoide.android.aptoidegames.installer.database.model.AppDetailsData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class AppDetailsRepository @Inject constructor(
  private val appDetailsDao: AppDetailsDao
) {

  fun getAsFlow(packageName: String): Flow<AppDetails> =
    appDetailsDao.getByPackageAsFlow(packageName).filterNotNull().mapNotNull { appDetailsData ->
      appDetailsData.name?.let {
        AppDetails(
          name = appDetailsData.name,
          iconUrl = appDetailsData.icon,
          icon = null
        )
      }
    }

  suspend fun get(packageName: String): AppDetails? =
    appDetailsDao.getByPackage(packageName)?.let {
      if (it.name != null) {
        AppDetails(
          name = it.name,
          iconUrl = it.icon,
          icon = null,
        )
      } else {
        null
      }
    }

  suspend fun save(packageName: String, details: AppDetails) =
    appDetailsDao.save(
      AppDetailsData(
        packageName = packageName,
        name = details.name,
        icon = details.iconUrl,
      )
    )
}
