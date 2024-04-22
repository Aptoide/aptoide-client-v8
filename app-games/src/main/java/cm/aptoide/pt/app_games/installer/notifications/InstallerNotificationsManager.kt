package cm.aptoide.pt.app_games.installer.notifications

interface InstallerNotificationsManager {

  suspend fun initialize()
  fun onInstallationQueued(packageName: String)
}
