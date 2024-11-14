package cm.aptoide.pt.feature_updates.presentation

import cm.aptoide.pt.feature_apps.data.model.AppJSON

interface UpdatesNotificationProvider {
  suspend fun showUpdatesNotification(updates: List<AppJSON>)
}
