package cm.aptoide.pt.feature_updates.domain

import cm.aptoide.pt.extensions.compatVersionCode
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_updates.presentation.UpdatesNotificationProvider
import cm.aptoide.pt.install_manager.InstallManager
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AutoUpdateGameInstallObserver @Inject constructor(
  private val updatesNotificationBuilder: UpdatesNotificationProvider,
  private val installManager: InstallManager
) {

  suspend fun observeInstall(update: App, installedVersionCode: Long) {
    installManager.getApp(update.packageName)
      .packageInfoFlow
      .filterNotNull()
      .first { it.compatVersionCode > installedVersionCode }
      .let { updatesNotificationBuilder.showSuccessAutoUpdatedGameNotification(update) }
  }
}
