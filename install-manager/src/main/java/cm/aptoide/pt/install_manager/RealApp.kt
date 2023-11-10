package cm.aptoide.pt.install_manager

import android.content.pm.PackageInfo
import androidx.core.content.pm.PackageInfoCompat
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.repository.PackageInfoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

internal class RealApp(
  override val packageName: String,
  packageInfo: PackageInfo?,
  private val taskFactory: Task.Factory,
  private val packageInfoRepository: PackageInfoRepository,
  scope: CoroutineScope,
) : App {

  private val _packageInfo = MutableSharedFlow<PackageInfo?>(replay = 1)

  private val _tasks = MutableSharedFlow<Task?>(replay = 1)

  override val packageInfo: Flow<PackageInfo?> = _packageInfo

  override val tasks: Flow<Task?> get() = _tasks

  init {
    scope.launch {
      _tasks.emit(null)
      _packageInfo.emit(packageInfo ?: packageInfoRepository.get(packageName))
    }
  }

  internal suspend fun update() {
    _packageInfo.emit(packageInfoRepository.get(packageName))
  }

  override suspend fun install(installPackageInfo: InstallPackageInfo): Task = when {
    _tasks.first() != null -> {
      throw IllegalStateException("Another task is already queued")
    }

    installPackageInfo.versionCode == getVersionCode() -> {
      throw IllegalArgumentException("This version is already installed")
    }

    installPackageInfo.versionCode < (getVersionCode() ?: Long.MIN_VALUE) -> {
      throw IllegalArgumentException("Newer version is installed")
    }

    else -> {
      taskFactory.createTask(
        packageName = packageName,
        type = Task.Type.INSTALL,
        installPackageInfo = installPackageInfo,
        onTerminate = { _tasks.emit(null) }
      ).also { _tasks.emit(it) }
    }
  }

  override suspend fun uninstall(): Task {
    if (_tasks.first() != null) throw IllegalStateException("Another task is already queued")
    val version =
      getVersionCode() ?: throw IllegalStateException("The $packageName is not installed")
    return taskFactory.createTask(
      packageName = packageName,
      type = Task.Type.UNINSTALL,
      installPackageInfo = InstallPackageInfo(version),
      onTerminate = { _tasks.emit(null) },
    ).also { _tasks.emit(it) }
  }

  override fun toString(): String = packageName

  private suspend fun getVersionCode() =
    _packageInfo.first()?.let(PackageInfoCompat::getLongVersionCode)
}
