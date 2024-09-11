package com.aptoide.android.aptoidegames.installer

import android.content.pm.PackageManager
import cm.aptoide.pt.install_manager.App
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppDetailsUseCase @Inject constructor(
  private val packageManager: PackageManager,
  private val repository: AppDetailsRepository,
) {
  suspend fun getAppDetails(app: App?): AppDetails? = app?.run {
    val appDetails = repository.get(packageName)

    packageInfo?.let {
      AppDetails(
        appId = appDetails?.appId,
        packageName = packageName,
        name = it.applicationInfo?.loadLabel(packageManager).toString(),
        icon = it.applicationInfo?.loadIcon(packageManager),
        iconUrl = appDetails?.iconUrl,
      )
    } ?: appDetails
  }

  suspend fun setAppDetails(app: cm.aptoide.pt.feature_apps.data.App) = app.run {
    val current = repository.get(packageName)
    repository.save(
      packageName,
      AppDetails(
        appId = appId,
        packageName = packageName,
        name = current?.name ?: name,
        icon = current?.icon,
        iconUrl = current?.iconUrl ?: icon,
      )
    )
  }
}
