package cm.aptoide.pt.install_manager

import cm.aptoide.pt.install_manager.dto.Constraints
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.TaskInfo
import cm.aptoide.pt.install_manager.environment.FreeSpaceChecker
import cm.aptoide.pt.install_manager.repository.TaskInfoRepository
import cm.aptoide.pt.install_manager.workers.PackageDownloader
import cm.aptoide.pt.install_manager.workers.PackageInstaller
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

internal class RealTask internal constructor(
  private val scope: CoroutineScope,
  taskInfo: TaskInfo,
  private val jobDispatcher: JobDispatcher,
  private val freeSpaceChecker: FreeSpaceChecker,
  private val packageDownloader: PackageDownloader,
  private val packageInstaller: PackageInstaller,
  private val taskInfoRepository: TaskInfoRepository,
) : Task {

  override val isFinished
    get() = _stateAndProgress.value.first in listOf(
      Task.State.ABORTED,
      Task.State.CANCELED,
      Task.State.COMPLETED,
      Task.State.FAILED
    )

  private var _taskInfo: TaskInfo = taskInfo

  override val packageName: String = _taskInfo.packageName

  override val type: Task.Type = _taskInfo.type

  private val installPackageInfo: InstallPackageInfo = _taskInfo.installPackageInfo

  override val constraints: Constraints get() = _taskInfo.constraints

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

  internal fun enqueue(alreadySaved: Boolean = false): Task {
    scope.launch {
      if (!alreadySaved) taskInfoRepository.saveJob(_taskInfo)
      jobDispatcher.enqueue(this@RealTask)
    }
    return this
  }

  internal suspend fun start() {
    if (isFinished) return // Already finished.
    val result = when (type) {
      Task.Type.UNINSTALL -> packageInstaller.uninstall(packageName)
        .collectErrorFor(Task.State.UNINSTALLING)

      else -> freeSpaceChecker.missingSpace(downloadSize)
        .takeIf { it > 0 }
        ?.let { Task.State.FAILED }
        ?: packageDownloader.download(packageName, installPackageInfo)
          .collectErrorFor(Task.State.DOWNLOADING)
        ?: _stateAndProgress.emit(Task.State.READY_TO_INSTALL to -1).let { null }
        ?: packageInstaller.install(packageName, installPackageInfo)
          .collectErrorFor(Task.State.INSTALLING)
    } ?: Task.State.COMPLETED
    _stateAndProgress.emit(result to -1)
    taskInfoRepository.removeAll(packageName)
  }

  override fun allowDownloadOnMetered() {
    scope.launch {
      if (_taskInfo.type == Task.Type.INSTALL && _stateAndProgress.value.first == Task.State.PENDING) {
        _taskInfo = _taskInfo.copy(
          constraints = _taskInfo.constraints.copy(
            networkType = Constraints.NetworkType.ANY
          )
        )
        taskInfoRepository.removeAll(packageName)
        taskInfoRepository.saveJob(_taskInfo)
        jobDispatcher.enqueue(this@RealTask)
      }
    }
  }

  override fun cancel() {
    scope.launch {
      if (isFinished) return@launch
      val cancelled = packageDownloader.cancel(packageName) || packageInstaller.cancel(packageName)
      if (!cancelled) {
        _stateAndProgress.tryEmit(Task.State.CANCELED to -1)
        taskInfoRepository.removeAll(packageName)
      }
    }
  }

  private suspend fun Flow<Int>.collectErrorFor(state: Task.State): Task.State? = try {
    onEach { _stateAndProgress.emit(state to it) }.collect()
    null
  } catch (e: AbortException) {
    Task.State.ABORTED
  } catch (e: CancellationException) {
    Task.State.CANCELED
  } catch (t: Throwable) {
    Task.State.FAILED
  }
}

