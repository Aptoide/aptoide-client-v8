package cm.aptoide.pt.feature_updates.presentation

interface UpdatesNotificationProvider {
  suspend fun showUpdatesNotification(numberOfUpdates: Int)
}
