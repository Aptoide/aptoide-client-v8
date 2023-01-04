package cm.aptoide.pt.install_manager

import android.content.pm.PackageInfo
import androidx.core.content.pm.PackageInfoCompat
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.repository.AppDetailsRepository
import cm.aptoide.pt.install_manager.repository.PackageInfoRepository
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class RealApp<D> private constructor(
  override val packageName: String,
  private val taskFactory: Task.Factory,
  private val packageInfoRepository: PackageInfoRepository,
  private val appDetailsRepository: AppDetailsRepository<D>,
  private val context: CoroutineContext,
) : App<D> {

  private var _packageInfo: PackageInfo? = null

  private var _details: D? = null

  private val _installedVersionCode get() = _packageInfo?.let(PackageInfoCompat::getLongVersionCode)

  override val packageInfo get() = _packageInfo

  override val details get() = _details

  override suspend fun getTask(): Task? = withContext(context) {
    taskFactory.getTask(packageName)
  }

  override suspend fun setDetails(details: D) = withContext(context) {
    appDetailsRepository.save(packageName, details)
    _details = details
  }

  override suspend fun removeDetails() = withContext(context) {
    appDetailsRepository.remove(packageName)
    _details = null
  }

  override suspend fun install(installPackageInfo: InstallPackageInfo): Task {
    return when {
      getTask() != null -> {
        throw IllegalStateException("another task is already queued")
      }
      installPackageInfo.versionCode == _installedVersionCode -> {
        throw IllegalArgumentException("This version is already installed")
      }
      installPackageInfo.versionCode < (_installedVersionCode ?: Long.MIN_VALUE) -> {
        throw IllegalArgumentException("Newer version is installed")
      }
      else -> {
        taskFactory.createTask(
          packageName = packageName,
          type = Task.Type.INSTALL,
          installPackageInfo = installPackageInfo,
          onTerminate = {
            _packageInfo = packageInfoRepository.get(packageName)
          }
        )
      }
    }
  }

  override suspend fun uninstall(): Task {
    if (getTask() != null) throw IllegalStateException("another task is already queued")
    val version = _installedVersionCode ?: throw IllegalStateException("$packageName not installed")
    return taskFactory.createTask(
      packageName = packageName,
      type = Task.Type.UNINSTALL,
      installPackageInfo = InstallPackageInfo(version),
      onTerminate = {
        _packageInfo = null
      },
    )
  }

  override fun toString(): String = packageName

  companion object {
    suspend fun <D> create(
      packageName: String,
      packageInfo: PackageInfo?,
      details: D?,
      taskFactory: Task.Factory,
      packageInfoRepository: PackageInfoRepository,
      appDetailsRepository: AppDetailsRepository<D>,
      context: CoroutineContext,
    ) = RealApp(
      packageName = packageName,
      taskFactory = taskFactory,
      packageInfoRepository = packageInfoRepository,
      appDetailsRepository = appDetailsRepository,
      context = context,
    ).apply {
      _details = details?.also { setDetails(it) } ?: appDetailsRepository.get(packageName)
      _packageInfo = packageInfo ?: packageInfoRepository.get(packageName)
    }
  }
}
