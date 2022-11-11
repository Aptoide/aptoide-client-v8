package cm.aptoide.pt.download_view.presentation

import cm.aptoide.pt.feature_apps.data.App

data class DownloadViewUiState(
  val app: App? = null,
  val downloadViewType: DownloadViewType = DownloadViewType.NO_APPCOINS,
  val downloadViewState: DownloadViewState = DownloadViewState.INSTALL,
  val downloadProgress: Int = 0
)
