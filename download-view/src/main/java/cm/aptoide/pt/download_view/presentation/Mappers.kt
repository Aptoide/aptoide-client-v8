package cm.aptoide.pt.download_view.presentation

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.install_manager.Task

fun App.getDownloadViewType() = if (isAppCoins) {
  DownloadViewType.APPCOINS
} else {
  DownloadViewType.NO_APPCOINS
}

fun DownloadViewUiState.copyWith(status: Pair<Task.State, Int>?) =
  copy(
    downloadViewState = when (status?.first) {
      null, Task.State.CANCELED -> DownloadViewState.INSTALL
      Task.State.PENDING -> DownloadViewState.PROCESSING
      Task.State.DOWNLOADING -> DownloadViewState.DOWNLOADING
      Task.State.INSTALLING,
      Task.State.UNINSTALLING -> DownloadViewState.INSTALLING
      Task.State.COMPLETED -> DownloadViewState.INSTALLED
      Task.State.FAILED -> DownloadViewState.ERROR
      Task.State.READY_TO_INSTALL -> DownloadViewState.READY_TO_INSTALL
    },
    downloadProgress = status?.second ?: 0
  )
