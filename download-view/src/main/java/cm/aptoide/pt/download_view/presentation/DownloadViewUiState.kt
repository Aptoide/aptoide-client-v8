package cm.aptoide.pt.download_view.presentation

data class DownloadViewUiState(
  val appSize: Long,
  val downloadViewType: DownloadViewType = DownloadViewType.NO_APPCOINS,
  val downloadViewState: DownloadViewState = DownloadViewState.INSTALL,
  val downloadProgress: Int = 0
)
