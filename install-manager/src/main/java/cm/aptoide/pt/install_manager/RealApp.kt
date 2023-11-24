package cm.aptoide.pt.install_manager

import android.content.pm.PackageInfo
import androidx.core.content.pm.PackageInfoCompat
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.repository.PackageInfoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion

internal class RealApp(
  override val packageName: String,
  packageInfo: PackageInfo?,
  task: Task?,
  private val taskFactory: Task.Factory,
  private val packageInfoRepository: PackageInfoRepository,
) : App {

  private val _packageInfo = MutableStateFlow(packageInfo ?: packageInfoRepository.get(packageName))

  private val _tasks = MutableStateFlow(task)

  override val packageInfo: Flow<PackageInfo?> = _packageInfo

  @OptIn(ExperimentalCoroutinesApi::class)
  override val tasks: Flow<Task?> = _tasks.flatMapConcat { task ->
    task?.takeIf { !it.isFinished }
      ?.stateAndProgress
      ?.map<Any, Task?> { task }
      ?.distinctUntilChanged()
      ?.onCompletion { emit(null) }
      ?: flowOf(null)
  }

  internal fun update() {
    _packageInfo.tryEmit(packageInfoRepository.get(packageName))
  }

  override suspend fun canInstall(installPackageInfo: InstallPackageInfo): Throwable? = when {
    tasks.first() != null -> IllegalStateException("Another task is already queued")

    installPackageInfo.versionCode == getVersionCode() ->
      IllegalArgumentException("This version is already installed")

    installPackageInfo.versionCode < (getVersionCode() ?: Long.MIN_VALUE) ->
      IllegalArgumentException("Newer version is installed")

    else -> null
  }

  override suspend fun canUninstall(): Throwable? = when {
    tasks.first() != null -> IllegalStateException("Another task is already queued")
    _packageInfo.first() == null -> IllegalStateException("The $packageName is not installed")
    else -> null
  }

  override suspend fun install(installPackageInfo: InstallPackageInfo): Task =
    canInstall(installPackageInfo)
      ?.let { throw it }
      ?: taskFactory.enqueue(
        packageName = packageName,
        type = Task.Type.INSTALL,
        installPackageInfo = installPackageInfo,
      ).also { _tasks.emit(it) }

  override suspend fun uninstall(): Task = canUninstall()
    ?.let { throw it }
    ?: taskFactory.enqueue(
      packageName = packageName,
      type = Task.Type.UNINSTALL,
      installPackageInfo = InstallPackageInfo(getVersionCode()!!),
    ).also { _tasks.emit(it) }

  override fun toString(): String = packageName

  private suspend fun getVersionCode() =
    _packageInfo.first()?.let(PackageInfoCompat::getLongVersionCode)
}
