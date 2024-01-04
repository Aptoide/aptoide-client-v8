package cm.aptoide.pt.download_view.presentation

import cm.aptoide.pt.install_manager.dto.Constraints
import cm.aptoide.pt.install_manager.dto.Constraints.NetworkType.ANY

val defaultResolver: ConstraintsResolver = { _, onResult ->
  onResult(
    Constraints(
      checkForFreeSpace = true,
      networkType = ANY
    )
  )
}

sealed class DownloadUiState {
  data class Install(
    val resolver: ConstraintsResolver = defaultResolver,
    val installWith: (resolver: ConstraintsResolver) -> Unit,
  ) : DownloadUiState() {
    val install: () -> Unit
      get() = { installWith(resolver) }
  }

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
    val resolver: ConstraintsResolver = defaultResolver,
    val updateWith: (resolver: ConstraintsResolver) -> Unit,
    val uninstall: () -> Unit,
  ) : DownloadUiState() {
    val update: () -> Unit
      get() = { updateWith(resolver) }
  }

  data class Error(
    val resolver: ConstraintsResolver = defaultResolver,
    val retryWith: (resolver: ConstraintsResolver) -> Unit,
  ) : DownloadUiState() {
    val retry: () -> Unit
      get() = { retryWith(resolver) }
  }

  data class ReadyToInstall(
    val cancel: () -> Unit,
  ) : DownloadUiState()
}

enum class ExecutionBlocker {
  QUEUE,
  CONNECTION,
  UNMETERED,
}
