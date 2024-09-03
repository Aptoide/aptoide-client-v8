package com.aptoide.android.aptoidegames.installer

import com.aptoide.android.aptoidegames.installer.database.AppDetailsDao
import com.aptoide.android.aptoidegames.installer.database.model.AppDetailsData
import javax.inject.Inject

class AppDetailsRepository @Inject constructor(
  private val appDetailsDao: AppDetailsDao
) {

  suspend fun get(packageName: String): AppDetails? =
    appDetailsDao.getByPackage(packageName)?.let {
      if (it.name != null) {
        AppDetails(
          appId = it.appId,
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
        appId = details.appId,
        name = details.name,
        icon = details.iconUrl,
      )
    )
}
