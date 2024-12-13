package cm.aptoide.pt.install_manager

import cm.aptoide.pt.install_manager.dto.Constraints
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.SizeEstimator
import cm.aptoide.pt.install_manager.dto.TaskInfo
import cm.aptoide.pt.install_manager.repository.TaskInfoRepository
import cm.aptoide.pt.install_manager.workers.PackageDownloader
import cm.aptoide.pt.install_manager.workers.PackageInstaller
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

internal class RealTask internal constructor(
  private val scope: CoroutineScope,
  private val appsCache: AppsCache,
  internal var taskInfo: TaskInfo,
  private val hasEnoughSpace: (Long) -> Boolean,
  private val sizeEstimator: SizeEstimator,
  private val jobDispatcher: JobDispatcher,
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

  override val state: Task.State
    get() = _stateAndProgress.value.first

  override val stateAndProgress: Flow<Pair<Task.State, Int>> = _stateAndProgress.transformWhile {
    emit(it)
    it.first !in listOf(
      Task.State.ABORTED,
      Task.State.CANCELED,
      Task.State.COMPLETED,
      Task.State.OUT_OF_SPACE,
      Task.State.FAILED
    )
  }

  private var job: Job? = null

  internal fun enqueue(alreadySaved: Boolean = false): Task {
    scope.launch {
      if (!alreadySaved) taskInfoRepository.saveJob(taskInfo)
      jobDispatcher.enqueue(this@RealTask)
    }
    return this
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  internal suspend fun start() {
    if (isFinished) return // Already finished.
    job = when (type) {
      Task.Type.UNINSTALL -> packageInstaller.uninstall(packageName)
        .onStart { _stateAndProgress.emit(Task.State.UNINSTALLING to -1) }
        .onEach { _stateAndProgress.emit(state to it) }

      Task.Type.INSTALL -> {
        listOf(
          packageDownloader.download(packageName, installPackageInfo)
            .onStart {
              _stateAndProgress.emit(Task.State.DOWNLOADING to -1)
              checkSizeToState(sizeEstimator.getDownloadSize(installPackageInfo))
            }
            .onEach { _stateAndProgress.emit(Task.State.DOWNLOADING to it) },
          packageInstaller.install(packageName, installPackageInfo)
            .onStart {
              _stateAndProgress.emit(Task.State.READY_TO_INSTALL to -1)
              checkSizeToState(sizeEstimator.getInstallSize(installPackageInfo))
            }
            .onEach { _stateAndProgress.emit(Task.State.INSTALLING to it) }
        )
          .asFlow()
          .flattenConcat()
      }
    }
      .onCompletion {
        val result = when (it) {
          null -> Task.State.COMPLETED
          is AbortException -> Task.State.ABORTED
          is CancellationException -> Task.State.CANCELED
          is OutOfSpaceException -> Task.State.OUT_OF_SPACE
          else -> Task.State.FAILED
        }
        _stateAndProgress.emit(result to -1)
      }
      .catch { /* Suppress failures */ }
      .launchIn(scope)
    job?.join()
    job = null
    taskInfoRepository.remove(taskInfo)
    appsCache.setBusy(packageName, false)
  }

  override fun allowDownloadOnMetered() {
    if (taskInfo.type == Task.Type.INSTALL && state == Task.State.PENDING) {
      scope.launch {
        //Remove old task
        taskInfoRepository.remove(taskInfo)

        //Create new task
        taskInfo = taskInfo.copy(
          constraints = taskInfo.constraints.copy(
            networkType = Constraints.NetworkType.ANY
          )
        )
        taskInfoRepository.saveJob(taskInfo)
        jobDispatcher.enqueue(this@RealTask)
      }
      _stateAndProgress.tryEmit(Task.State.PENDING to -2)
    }
  }

  override fun cancel() {
    scope.launch {
      if (isFinished) return@launch
      job?.cancel() ?: run {
        _stateAndProgress.tryEmit(Task.State.CANCELED to -1)
        taskInfoRepository.remove(taskInfo)
        appsCache.setBusy(packageName, false)
      }
    }
  }

  private fun checkSizeToState(size: Long) {
    if (!hasEnoughSpace(size)) throw OutOfSpaceException(size, "")
  }
}

