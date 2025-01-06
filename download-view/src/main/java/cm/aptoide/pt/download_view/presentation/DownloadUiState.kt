package cm.aptoide.pt.download_view.presentation

import cm.aptoide.pt.install_manager.dto.Constraints
import cm.aptoide.pt.install_manager.dto.Constraints.NetworkType.ANY
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.InstallationFile
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.random.nextLong

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
    val installPackageInfo: InstallPackageInfo,
    val blocker: ExecutionBlocker = ExecutionBlocker.QUEUE,
    val action: (() -> Unit)?,
  ) : DownloadUiState()

  data class Downloading(
    val installPackageInfo: InstallPackageInfo,
    val downloadProgress: Int = 0,
    val cancel: () -> Unit,
  ) : DownloadUiState()

  data class Installing(
    val installPackageInfo: InstallPackageInfo,
    val installProgress: Int = 0,
  ) : DownloadUiState()

  data class Uninstalling(
    val installPackageInfo: InstallPackageInfo,
  ) : DownloadUiState()

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

  data class Migrate(
    val resolver: ConstraintsResolver = defaultResolver,
    val open: () -> Unit,
    val uninstall: () -> Unit,
    val migrateWith: (resolver: ConstraintsResolver) -> Unit,
  ) : DownloadUiState() {
    val migrate: () -> Unit
      get() = { migrateWith(resolver) }
  }

  data class ReadyToInstall(
    val installPackageInfo: InstallPackageInfo = InstallPackageInfo(0),
    val cancel: () -> Unit,
  ) : DownloadUiState()
}

enum class ExecutionBlocker {
  QUEUE,
  CONNECTION,
  UNMETERED,
}

val downloadUiStates: List<DownloadUiState>
  get() = listOf(
    DownloadUiState.Install(
      installWith = {}
    ),
    DownloadUiState.Outdated(
      open = {},
      updateWith = {},
      uninstall = {}
    ),
    DownloadUiState.Migrate(
      open = {},
      uninstall = {},
      migrateWith = {}
    ),
    DownloadUiState.Waiting(
      installPackageInfo = randomInstallPackageInfo,
      blocker = ExecutionBlocker.QUEUE,
      action = null
    ),
    DownloadUiState.Waiting(
      installPackageInfo = randomInstallPackageInfo,
      blocker = ExecutionBlocker.CONNECTION,
      action = null
    ),
    DownloadUiState.Waiting(
      installPackageInfo = randomInstallPackageInfo,
      blocker = ExecutionBlocker.UNMETERED,
      action = null
    ),
    DownloadUiState.Downloading(
      installPackageInfo = randomInstallPackageInfo,
      downloadProgress = -1,
      cancel = {}
    ),
    DownloadUiState.Downloading(
      installPackageInfo = randomInstallPackageInfo,
      downloadProgress = Random.nextInt(0..100),
      cancel = {}
    ),
    DownloadUiState.ReadyToInstall(
      installPackageInfo = randomInstallPackageInfo,
      cancel = {}
    ),
    DownloadUiState.Installing(
      installPackageInfo = randomInstallPackageInfo,
      installProgress = -1,
    ),
    DownloadUiState.Installing(
      installPackageInfo = randomInstallPackageInfo,
      installProgress = Random.nextInt(0..100),
    ),
    DownloadUiState.Uninstalling(
      installPackageInfo = randomInstallPackageInfo
    ),
    DownloadUiState.Installed(
      open = {},
      uninstall = {}
    ),
    DownloadUiState.Error(
      retryWith = {}
    )
  )

val randomInstallPackageInfo
  get() = InstallPackageInfo(
    versionCode = 0,
    installationFiles = setOf(
      InstallationFile(
        name = "",
        type = InstallationFile.Type.BASE,
        md5 = "",
        fileSize = Random.nextLong(5000000L..300000000L),
        url = "",
        altUrl = "",
        localPath = ""
      )
    )
  )
