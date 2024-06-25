package com.aptoide.android.aptoidegames.installer.analytics

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.install_manager.App
import cm.aptoide.pt.install_manager.InstallManager
import cm.aptoide.pt.install_manager.Task.State
import cm.aptoide.pt.install_manager.Task.State.PENDING
import cm.aptoide.pt.install_manager.dto.Constraints.NetworkType
import cm.aptoide.pt.install_manager.environment.NetworkConnection
import cm.aptoide.pt.network_listener.NetworkConnectionImpl
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val scheduledDownloadsListener: ScheduledDownloadsListenerImpl,
) : ViewModel()

@Composable
fun rememberScheduledInstalls(): ScheduledDownloadsListener = runPreviewable(
  preview = { object : ScheduledDownloadsListener {} },
  real = {
    val injectionsProvider = hiltViewModel<InjectionsProvider>()
    injectionsProvider.scheduledDownloadsListener
  }
)

interface ScheduledDownloadsListener {
  fun listenToWifiStart(packageName: String) {}
  fun listenToWifiStart(app: App) {}
}

class ScheduledDownloadsListenerImpl @Inject constructor(
  private val analytics: GenericAnalytics,
  private val installManager: InstallManager,
  private val networkConnection: NetworkConnectionImpl,
) : ScheduledDownloadsListener, CoroutineScope {

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO + Job()

  fun initialize() {
    installManager.scheduledApps.forEach { listenToWifiStart(it) }
  }

  override fun listenToWifiStart(packageName: String) {
    listenToWifiStart(installManager.getApp(packageName))
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  override fun listenToWifiStart(app: App) {
    this.launch {
      app.taskFlow.filterNotNull().first()
        .takeIf { it.state == PENDING }
        ?.takeIf { it.constraints.networkType == NetworkType.UNMETERED }
        ?.let { task ->
          val analyticsPayload = task.installPackageInfo.payload.toAnalyticsPayload()!!
          networkConnection.states
            .map { it != NetworkConnection.State.UNMETERED }
            .distinctUntilChanged()
            .filter { it }
            .flatMapLatest { _ ->
              task.takeIf { it.state == PENDING }
                ?.stateAndProgress
                ?.map { (state) -> state != State.DOWNLOADING }
                ?.takeWhile { it }
                ?.onCompletion {
                  if (it == null && task.constraints.networkType == NetworkType.UNMETERED) {
                    analytics.sendDownloadRestartedEvent(app.packageName, analyticsPayload)
                  }
                } ?: flow { emit(false) }
            }
            .takeWhile { it }
            .launchIn(this)
        }
    }
  }
}
