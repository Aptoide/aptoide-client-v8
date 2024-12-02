package cm.aptoide.pt.feature_updates.presentation

import cm.aptoide.pt.feature_apps.data.App

interface UpdatesNotificationProvider {
  suspend fun showUpdatesNotification(updates: List<App>) {}
}
