package cm.aptoide.pt.install_manager

import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.TaskInfo
import cm.aptoide.pt.install_manager.repository.TaskInfoRepository
import cm.aptoide.pt.install_manager.workers.PackageDownloader
import cm.aptoide.pt.install_manager.workers.PackageInstaller
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.transformWhile

internal class RealTask internal constructor(
  val packageName: String,
  override val type: Task.Type,
  val installPackageInfo: InstallPackageInfo,
  private val onTerminate: suspend (success: Boolean) -> Unit,
  private val jobDispatcher: JobDispatcher,
  private val packageDownloader: PackageDownloader,
  private val packageInstaller: PackageInstaller,
  private val taskInfoRepository: TaskInfoRepository,
  private val clock: Clock,
) : Task {

  override var isFinished = false
    private set

  private val _stateAndProgress = MutableStateFlow(Task.State.PENDING to -1)

  override val stateAndProgress: Flow<Pair<Task.State, Int>>
    get() = _stateAndProgress
      .transformWhile {
        emit(it)
        it.first !in listOf(
          Task.State.ABORTED,
          Task.State.CANCELED,
          Task.State.COMPLETED,
          Task.State.FAILED
        )
      }

  internal suspend fun enqueue(forceDownload: Boolean) {
    if (forceDownload) {
      taskInfoRepository.saveJob(
        TaskInfo(
          packageName,
          installPackageInfo,
          type,
          clock.getCurrentTimeStamp()
        )
      )
    }
    when (type) {
      Task.Type.INSTALL -> jobDispatcher.enqueue(this) { performInstall(forceDownload) }
      Task.Type.UNINSTALL -> jobDispatcher.enqueue(this, ::performUninstall)
    }
  }

  override suspend fun cancel() {
    if (isFinished) return // No op.
    val cancelled = packageDownloader.cancel(packageName) || packageInstaller.cancel(packageName)
    if (!cancelled) {
      finalize(Task.State.CANCELED)
    }
  }

  private suspend fun performInstall(forceDownload: Boolean) = tryToPerform {
    packageDownloader.download(packageName, forceDownload, installPackageInfo).collect {
      _stateAndProgress.emit(Task.State.DOWNLOADING to it)
    }
    _stateAndProgress.emit(Task.State.READY_TO_INSTALL to -1)
    packageInstaller.install(packageName, installPackageInfo).collect {
      _stateAndProgress.emit(Task.State.INSTALLING to it)
    }
  }

  private suspend fun performUninstall() = tryToPerform {
    packageInstaller.uninstall(packageName).collect {
      _stateAndProgress.emit(Task.State.UNINSTALLING to it)
    }
  }

  private suspend fun tryToPerform(action: suspend () -> Unit) {
    if (isFinished) return // Already finished.
    try {
      action()
      finalize(Task.State.COMPLETED)
    } catch (e: AbortException) {
      finalize(Task.State.ABORTED)
    } catch (e: CancellationException) {
      finalize(Task.State.CANCELED)
    } catch (e: Throwable) {
      finalize(Task.State.FAILED)
    }
  }

  private suspend fun finalize(state: Task.State) {
    isFinished = true
    _stateAndProgress.emit(state to -1)
    onTerminate.invoke(state == Task.State.COMPLETED)
    taskInfoRepository.removeAll(packageName)
  }
}

