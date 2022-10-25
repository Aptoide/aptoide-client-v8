package cm.aptoide.pt.install_manager

import cm.aptoide.pt.install_manager.dto.AppInfo
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.Version
import cm.aptoide.pt.install_manager.repository.AppInfoRepository
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class RealApp<D> internal constructor(
  override val packageName: String,
  private var isKnown: Boolean = false,
  private var _installedVersion: Version? = null,
  private var _details: D? = null,
  private val taskFactory: Task.Factory,
  private val appInfoRepository: AppInfoRepository<D>,
  private val context: CoroutineContext,
) : App<D> {

  override val installedVersionName get() = _installedVersion?.versionName

  override val installedVersionCode get() = _installedVersion?.versionCode

  override val details get() = _details

  override suspend fun getTask(): Task? = withContext(context) {
    taskFactory.getTask(packageName)
  }

  override suspend fun setDetails(details: D) = withContext(context) {
    if (isKnown) {
      appInfoRepository.save(AppInfo(packageName, _installedVersion, details))
    }
    _details = details
  }

  override suspend fun install(installPackageInfo: InstallPackageInfo): Task {
    return when {
      getTask() != null -> {
        throw IllegalStateException("another task is already queued")
      }
      installPackageInfo.version.versionCode == _installedVersion?.versionCode -> {
        throw IllegalArgumentException("This version is already installed")
      }
      installPackageInfo.version.versionCode < (_installedVersion?.versionCode
        ?: Int.MIN_VALUE) -> {
        throw IllegalArgumentException("Newer version is installed")
      }
      else -> {
        taskFactory.createTask(
          packageName = packageName,
          type = Task.Type.INSTALL,
          installPackageInfo = installPackageInfo,
          onTerminate = {
            if (it) {
              isKnown = true
              appInfoRepository.save(AppInfo(packageName, installPackageInfo.version, _details))
              _installedVersion = installPackageInfo.version
            }
          }
        )
      }
    }
  }

  override suspend fun uninstall(): Task {
    if (getTask() != null) throw IllegalStateException("another task is already queued")
    val version = _installedVersion ?: throw IllegalStateException("$packageName not installed")
    return taskFactory.createTask(
      packageName = packageName,
      type = Task.Type.UNINSTALL,
      installPackageInfo = InstallPackageInfo(version),
      onTerminate = {
        if (it) {
          appInfoRepository.save(AppInfo(packageName, null, _details))
          _installedVersion = null
        }
      },
    )
  }

  override suspend fun remove() = withContext(context) {
    isKnown = false
    appInfoRepository.remove(packageName).also { getTask()?.cancel() }
  }

  override fun toString(): String = packageName
}
