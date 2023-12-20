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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

internal class RealTask internal constructor(
  private val scope: CoroutineScope,
  private val appsCache: AppsCache,
  internal var taskInfo: TaskInfo,
  private val jobDispatcher: JobDispatcher,
  private val freeSpaceChecker: FreeSpaceChecker,
  private val packageDownloader: PackageDownloader,
  private val packageInstaller: PackageInstaller,
  private val taskInfoRepository: TaskInfoRepository,
) : Task {

  override val isFinished
    get() = state in listOf(
      Task.State.ABORTED,
      Task.State.CANCELED,
      Task.State.COMPLETED,
      Task.State.FAILED
    )

  override val packageName: String = this.taskInfo.packageName

  override val type: Task.Type = this.taskInfo.type

  override val installPackageInfo: InstallPackageInfo = this.taskInfo.installPackageInfo

  override val constraints: Constraints get() = taskInfo.constraints

  private val _stateAndProgress = MutableStateFlow(Task.State.PENDING to -1)

  internal val downloadSize = installPackageInfo.downloadSize

  override val state: Task.State
    get() = _stateAndProgress.value.first

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
      if (!alreadySaved) taskInfoRepository.saveJob(taskInfo)
      jobDispatcher.enqueue(this@RealTask)
    }
    return this
  }

  internal suspend fun start() {
    if (isFinished) return // Already finished.
    val result = when (type) {
      Task.Type.UNINSTALL -> packageInstaller.uninstall(packageName)
        .onStart { _stateAndProgress.emit(Task.State.UNINSTALLING to -1) }
        .collectErrorFor(Task.State.UNINSTALLING)

      else -> freeSpaceChecker.missingSpace(downloadSize)
        .takeIf { it > 0 }
        ?.let { Task.State.FAILED }
        ?: packageDownloader.download(packageName, installPackageInfo)
          .onStart { _stateAndProgress.emit(Task.State.DOWNLOADING to -1) }
          .collectErrorFor(Task.State.DOWNLOADING)
        ?: _stateAndProgress.emit(Task.State.READY_TO_INSTALL to -1).let { null }
        ?: packageInstaller.install(packageName, installPackageInfo)
          .collectErrorFor(Task.State.INSTALLING)
    } ?: Task.State.COMPLETED
    _stateAndProgress.emit(result to -1)
    taskInfoRepository.removeAll(packageName)
    appsCache.setBusy(packageName, false)
  }

  override fun allowDownloadOnMetered() {
    scope.launch {
      if (taskInfo.type == Task.Type.INSTALL && state == Task.State.PENDING) {
        taskInfo = taskInfo.copy(
          constraints = taskInfo.constraints.copy(
            networkType = Constraints.NetworkType.ANY
          )
        )
        taskInfoRepository.removeAll(packageName)
        taskInfoRepository.saveJob(taskInfo)
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
        appsCache.setBusy(packageName, false)
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

