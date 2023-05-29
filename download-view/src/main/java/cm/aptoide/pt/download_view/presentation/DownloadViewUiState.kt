package cm.aptoide.pt.download_view.presentation

data class DownloadViewUiState(
  val downloadViewState: DownloadViewState = DownloadViewState.INSTALL,
  val downloadProgress: Int = 0
)
