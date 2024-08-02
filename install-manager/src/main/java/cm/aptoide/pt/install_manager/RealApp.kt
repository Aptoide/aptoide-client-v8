package cm.aptoide.pt.install_manager

import android.content.pm.PackageInfo
import androidx.core.content.pm.PackageInfoCompat
import cm.aptoide.pt.install_manager.dto.Constraints
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.SizeEstimator
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
  private val getMissingSpace: (Long) -> Long,
  private val sizeEstimator: SizeEstimator,
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

  override fun install(
    installPackageInfo: InstallPackageInfo,
    constraints: Constraints,
  ): Task {
    if (task != null) throw IllegalStateException("Another task is already queued")
    val vcDiff = (versionCode ?: 0) - installPackageInfo.versionCode
    if (vcDiff == 0L) throw IllegalArgumentException("This version is already installed")
    if (vcDiff > 0L) throw IllegalArgumentException("Newer version is installed")
    val missingSpace = getMissingSpace(sizeEstimator.getTotalInstallationSize(installPackageInfo))
    if (constraints.checkForFreeSpace && missingSpace > 0)
      throw OutOfSpaceException(
        missingSpace = missingSpace,
        message = "Not enough free space to download and install"
      )
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
