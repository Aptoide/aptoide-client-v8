package cm.aptoide.pt.download_view.presentation

import cm.aptoide.pt.feature_apps.data.DetailedApp

data class DownloadViewUiState(
  val app: DetailedApp?,
  val downloadViewType: DownloadViewType,
  val downloadViewState: DownloadViewState,
  val downloadProgress: Int
)
