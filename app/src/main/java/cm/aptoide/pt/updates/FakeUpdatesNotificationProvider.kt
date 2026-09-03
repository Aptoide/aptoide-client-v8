package cm.aptoide.pt.updates

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_updates.presentation.UpdatesNotificationProvider

class FakeUpdatesNotificationProvider : UpdatesNotificationProvider {
  override suspend fun showVIPUpdateNotification(app: App) = Unit
  override suspend fun showSuccessAutoUpdatedGameNotification(app: App) = Unit
}
