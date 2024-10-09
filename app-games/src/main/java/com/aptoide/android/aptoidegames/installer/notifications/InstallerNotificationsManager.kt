package com.aptoide.android.aptoidegames.installer.notifications

interface InstallerNotificationsManager {

  suspend fun initialize()
  fun onInstallationQueued(packageName: String)
  fun onReadyToInstall(packageName: String)
}
