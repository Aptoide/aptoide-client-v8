package cm.aptoide.pt.install_manager

import android.content.pm.PackageInfo
import cm.aptoide.pt.extensions.compatVersionCode
import cm.aptoide.pt.install_manager.dto.Constraints
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.SizeEstimator
import cm.aptoide.pt.install_manager.repository.AppInfoRepository
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
  updatesOwnerPackageName: String?,
  private val taskFactory: Task.Factory,
  private val getMissingSpace: (Long) -> Long,
  private val sizeEstimator: SizeEstimator,
  private val appInfoRepository: AppInfoRepository,
) : App {

  private val _packageInfo =
    MutableStateFlow(packageInfo ?: appInfoRepository.getPackageInfo(packageName))

  private val versionCode get() = _packageInfo.value?.compatVersionCode

  internal val tasks = MutableStateFlow<Task?>(null)

  override val packageInfo: PackageInfo?
    get() = _packageInfo.value

  override var updatesOwnerPackageName: String? =
    updatesOwnerPackageName ?: appInfoRepository.getUpdateOwnerPackageName(packageName)

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
    _packageInfo.value = appInfoRepository.getPackageInfo(packageName)
    updatesOwnerPackageName = appInfoRepository.getUpdateOwnerPackageName(packageName)
  }

  override fun canInstall(
    installPackageInfo: InstallPackageInfo,
    constraints: Constraints
  ): Throwable? {
    if (task != null) return IllegalStateException("Another task is already queued")
    val vcDiff = (versionCode ?: 0) - installPackageInfo.versionCode
    if (vcDiff == 0L) return IllegalArgumentException("This version is already installed")
    if (vcDiff > 0L) return IllegalArgumentException("Newer version is installed")
    val missingSpace = getMissingSpace(sizeEstimator.getTotalInstallationSize(installPackageInfo))
    if (constraints.checkForFreeSpace && missingSpace > 0) {
      return OutOfSpaceException(
        missingSpace = missingSpace,
        message = "Not enough free space to download and install"
      )
    }

    return null
  }

  override fun install(
    installPackageInfo: InstallPackageInfo,
    constraints: Constraints,
  ): Task {
    canInstall(installPackageInfo, constraints)?.let {
      throw it
    }
    return taskFactory.enqueue(
      packageName = packageName,
      type = Task.Type.INSTALL,
      installPackageInfo = installPackageInfo,
      constraints = constraints,
    ).also { tasks.value = it }
  }

  override fun uninstall(constraints: Constraints): Task {
    if (task != null) throw IllegalStateException("Another task is already queued")
    val verCode = versionCode ?: throw IllegalStateException("The $packageName is not installed")
    return taskFactory.enqueue(
      packageName = packageName,
      type = Task.Type.UNINSTALL,
      installPackageInfo = InstallPackageInfo(verCode),
      constraints = constraints
    ).also { tasks.value = it }
  }

  override fun toString(): String = packageName
}
