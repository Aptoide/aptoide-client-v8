package cm.aptoide.pt.install_manager

import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.TaskInfo
import cm.aptoide.pt.install_manager.environment.FreeSpaceChecker
import cm.aptoide.pt.install_manager.repository.TaskInfoRepository
import cm.aptoide.pt.install_manager.workers.PackageDownloader
import cm.aptoide.pt.install_manager.workers.PackageInstaller
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.launch

internal class RealTask internal constructor(
  private val scope: CoroutineScope,
  internal val taskInfo: TaskInfo,
  private val freeSpaceChecker: FreeSpaceChecker,
  private val packageDownloader: PackageDownloader,
  private val packageInstaller: PackageInstaller,
  private val taskInfoRepository: TaskInfoRepository,
) : Task {

  override var isFinished = false
    private set

  override val packageName: String = taskInfo.packageName

  override val type: Task.Type = taskInfo.type

  private val installPackageInfo: InstallPackageInfo = taskInfo.installPackageInfo

  private val _stateAndProgress = MutableStateFlow(Task.State.PENDING to -1)

  internal val downloadSize = installPackageInfo.downloadSize

  override val stateAndProgress: Flow<Pair<Task.State, Int>> = _stateAndProgress.transformWhile {
    emit(it)
    it.first !in listOf(
      Task.State.ABORTED,
      Task.State.CANCELED,
      Task.State.COMPLETED,
      Task.State.FAILED
    )
  }

  internal suspend fun start() {
    if (isFinished) return // Already finished.
    when (type) {
      Task.Type.INSTALL -> flow<Pair<Task.State, Int>> {
        val missingSpace = freeSpaceChecker.missingSpace(downloadSize)
        if (missingSpace > 0) {
          throw OutOfSpaceException(missingSpace, "Not enough free space to download and install")
        }
      }
        .onCompletion { if (it == null) emitAll(downloadFlow) }
        .onCompletion { if (it == null) emitAll(installFlow) }

      Task.Type.UNINSTALL -> uninstallFlow
    }
      .catch {
        emit(
          when (it) {
            is AbortException -> Task.State.ABORTED to -1
            is CancellationException -> Task.State.CANCELED to -1
            else -> Task.State.FAILED to -1
          }
        )
      }
      .onEach { _stateAndProgress.emit(it) }
      .onCompletion { finalize() }
      .collect()
  }

  override fun cancel() {
    scope.launch {
      if (isFinished) return@launch
      val cancelled = packageDownloader.cancel(packageName) || packageInstaller.cancel(packageName)
      if (!cancelled) {
        _stateAndProgress.tryEmit(Task.State.CANCELED to -1)
        finalize()
      }
    }
  }

  private val downloadFlow: Flow<Pair<Task.State, Int>>
    get() = packageDownloader.download(packageName, installPackageInfo)
      .map { Task.State.DOWNLOADING to it }
      .onCompletion { if (it == null) emit(Task.State.READY_TO_INSTALL to -1) }

  private val installFlow: Flow<Pair<Task.State, Int>>
    get() = packageInstaller.install(packageName, installPackageInfo)
      .map { Task.State.INSTALLING to it }
      .onCompletion { if (it == null) emit(Task.State.COMPLETED to -1) }

  private val uninstallFlow: Flow<Pair<Task.State, Int>>
    get() = packageInstaller.uninstall(packageName)
      .map { Task.State.UNINSTALLING to it }
      .onCompletion { if (it == null) emit(Task.State.COMPLETED to -1) }

  private suspend fun finalize() {
    taskInfoRepository.removeAll(packageName)
    isFinished = true
  }
}

