package cm.aptoide.pt.download_view.presentation

sealed class DownloadUiState {
  object Install : DownloadUiState()
  object Processing : DownloadUiState()
  data class Downloading(val downloadProgress: Int = 0) : DownloadUiState()
  data class Installing(val downloadProgress: Int = 0) : DownloadUiState()
  object Uninstalling : DownloadUiState()
  object Installed : DownloadUiState()
  object Outdated : DownloadUiState()
  object Error : DownloadUiState()
  object ReadyToInstall : DownloadUiState()
}
