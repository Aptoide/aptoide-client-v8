package com.aptoide.android.aptoidegames.installer.autoopen

import cm.aptoide.pt.download_view.presentation.InstalledAppOpener
import cm.aptoide.pt.install_manager.InstallManager
import cm.aptoide.pt.install_manager.Task
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AutoOpenAfterInstallController @Inject constructor(
  private val installManager: InstallManager,
  private val installedAppOpener: InstalledAppOpener,
) {

  private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
  private val watchers = ConcurrentHashMap<String, Job>()

  fun open(packageName: String) {
    if (watchers.containsKey(packageName)) return
    watchers[packageName] = scope.launch {
      try {
        val task = installManager.getApp(packageName).taskFlow.filterNotNull().first()
        task.stateAndProgress.collect { state ->
          if (state == Task.State.Completed) {
            withContext(Dispatchers.Main) {
              installedAppOpener.openInstalledApp(packageName)
            }
          }
        }
      } catch (_: CancellationException) {
      } catch (t: Throwable) {
        Timber.e(t, "AutoOpen watcher crashed for %s", packageName)
      } finally {
        watchers.remove(packageName)
      }
    }
  }

  fun cancel(packageName: String) {
    watchers.remove(packageName)?.cancel()
  }
}
