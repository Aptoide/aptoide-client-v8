package cm.aptoide.pt.download_view.presentation

import android.content.pm.PackageInfo
import androidx.core.content.pm.PackageInfoCompat
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.install_manager.Task

fun DownloadViewUiState.copyWith(app: App, status: Pair<PackageInfo?, Pair<Task.State, Int>?>) =
  copy(
    downloadViewState = when (status.second?.first) {
      null -> if (downloadViewState == DownloadViewState.ERROR) {
        downloadViewState
      } else {
        status.first?.let {
          if (PackageInfoCompat.getLongVersionCode(it) < app.versionCode) {
            DownloadViewState.OUTDATED
          } else {
            DownloadViewState.INSTALLED
          }
        } ?: DownloadViewState.INSTALL
      }
      Task.State.CANCELED -> DownloadViewState.INSTALL
      Task.State.PENDING -> DownloadViewState.PROCESSING
      Task.State.DOWNLOADING -> DownloadViewState.DOWNLOADING
      Task.State.INSTALLING,
      Task.State.UNINSTALLING -> DownloadViewState.INSTALLING
      Task.State.COMPLETED -> DownloadViewState.INSTALLED
      Task.State.FAILED -> DownloadViewState.ERROR
      Task.State.READY_TO_INSTALL -> DownloadViewState.READY_TO_INSTALL
    },
    downloadProgress = status.second?.second ?: 0
  )
