package cm.aptoide.pt.app_games.installer.notifications

class FakeInstallerNotificationsManager : InstallerNotificationsManager {
  override suspend fun initialize() {}
  override fun onInstallationQueued(packageName: String) {}
}
