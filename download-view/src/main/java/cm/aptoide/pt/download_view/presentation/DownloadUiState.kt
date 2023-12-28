package cm.aptoide.pt.download_view.presentation

sealed class DownloadUiState {
  data class Install(
    val install: () -> Unit,
  ) : DownloadUiState()

  data class OutOfSpaceError(
    val clear: (() -> Unit),
  ) : DownloadUiState()

  data class WifiPrompt(
    val onAction: ((Boolean?) -> Unit),
  ) : DownloadUiState()

  data class Waiting(
    val blocker: ExecutionBlocker = ExecutionBlocker.QUEUE,
    val action: (() -> Unit)?,
  ) : DownloadUiState()

  data class Downloading(
    val size: Long = 0,
    val downloadProgress: Int = 0,
    val cancel: () -> Unit,
  ) : DownloadUiState()

  data class Installing(
    val size: Long = 0,
    val installProgress: Int = 0,
  ) : DownloadUiState()

  object Uninstalling : DownloadUiState()

  data class Installed(
    val open: () -> Unit,
    val uninstall: () -> Unit,
  ) : DownloadUiState()

  data class Outdated(
    val open: () -> Unit,
    val update: () -> Unit,
    val uninstall: () -> Unit,
  ) : DownloadUiState()

  data class Error(
    val retry: () -> Unit,
    val clear: () -> Unit,
  ) : DownloadUiState()

  data class ReadyToInstall(
    val cancel: () -> Unit,
  ) : DownloadUiState()
}

enum class ExecutionBlocker {
  QUEUE,
  CONNECTION,
  UNMETERED,
}
