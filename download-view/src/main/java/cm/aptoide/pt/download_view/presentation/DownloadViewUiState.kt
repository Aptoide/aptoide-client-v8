package cm.aptoide.pt.download_view.presentation

import cm.aptoide.pt.feature_apps.data.App

data class DownloadViewUiState(
  val app: App?,
  val downloadViewType: DownloadViewType,
  val downloadViewState: DownloadViewState,
  val downloadProgress: Int
)