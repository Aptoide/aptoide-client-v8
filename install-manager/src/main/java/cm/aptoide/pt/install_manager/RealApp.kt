package cm.aptoide.pt.install_manager

import android.content.pm.PackageInfo
import androidx.core.content.pm.PackageInfoCompat
import cm.aptoide.pt.install_manager.dto.Constraints
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.environment.FreeSpaceChecker
import cm.aptoide.pt.install_manager.repository.PackageInfoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion

internal class RealApp(
  override val packageName: String,
  packageInfo: PackageInfo?,
  private val taskFactory: Task.Factory,
  private val jobDispatcher: JobDispatcher,
  private val freeSpaceChecker: FreeSpaceChecker,
  private val packageInfoRepository: PackageInfoRepository,
) : App {

  private val _packageInfo = MutableStateFlow(packageInfo ?: packageInfoRepository.get(packageName))

  private val versionCode get() = _packageInfo.value?.let(PackageInfoCompat::getLongVersionCode)

  internal val tasks = MutableStateFlow<Task?>(null)

  override val packageInfo: PackageInfo?
    get() = _packageInfo.value

  override val packageInfoFlow: Flow<PackageInfo?> = _packageInfo

  override val task: Task?
    get() = tasks.value?.takeIf { !it.isFinished }

  @OptIn(ExperimentalCoroutinesApi::class)
  override val taskFlow: Flow<Task?> = tasks.flatMapConcat { task ->
    task?.takeIf { !it.isFinished }
      ?.stateAndProgress
      ?.map<Any, Task?> { task }
      ?.distinctUntilChanged()
      ?.onCompletion { emit(null) }
      ?: flowOf(null)
  }

  internal fun update() {
    _packageInfo.value = packageInfoRepository.get(packageName)
  }

  override fun canInstall(installPackageInfo: InstallPackageInfo): Throwable? {
    val versionCode = versionCode
    val missingSpase = freeSpaceChecker.missingSpace(
      appSize = installPackageInfo.downloadSize,
      scheduledSize = jobDispatcher.scheduledSize
    )
    return when {
      task != null -> IllegalStateException("Another task is already queued")

      installPackageInfo.versionCode == versionCode ->
        IllegalArgumentException("This version is already installed")

      installPackageInfo.versionCode < (versionCode ?: Long.MIN_VALUE) ->
        IllegalArgumentException("Newer version is installed")

      missingSpase > 0 -> OutOfSpaceException(
        missingSpace = missingSpase,
        message = "Not enough free space to download and install"
      )

      else -> null
    }
  }

  override fun canUninstall(): Throwable? = when {
    task != null -> IllegalStateException("Another task is already queued")
    _packageInfo.value == null -> IllegalStateException("The $packageName is not installed")
    else -> null
  }

  override fun install(
    installPackageInfo: InstallPackageInfo,
    constraints: Constraints,
  ): Task = canInstall(installPackageInfo)
    ?.takeUnless { !constraints.checkForFreeSpace && it is OutOfSpaceException }
    ?.let { throw it }
    ?: taskFactory.enqueue(
      packageName = packageName,
      type = Task.Type.INSTALL,
      installPackageInfo = installPackageInfo,
      constraints = constraints,
    ).also { tasks.value = it }

  override fun uninstall(constraints: Constraints): Task = canUninstall()
    ?.let { throw it }
    ?: taskFactory.enqueue(
      packageName = packageName,
      type = Task.Type.UNINSTALL,
      installPackageInfo = InstallPackageInfo(versionCode!!),
      constraints = constraints
    ).also { tasks.value = it }

  override fun toString(): String = packageName
}
