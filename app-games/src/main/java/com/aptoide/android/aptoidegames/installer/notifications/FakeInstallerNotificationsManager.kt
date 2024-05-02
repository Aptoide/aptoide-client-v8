package com.aptoide.android.aptoidegames.installer.notifications

class FakeInstallerNotificationsManager : InstallerNotificationsManager {
  override suspend fun initialize() {}
  override fun onInstallationQueued(packageName: String) {}
}
