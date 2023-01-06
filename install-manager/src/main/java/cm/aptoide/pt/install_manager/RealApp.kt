package cm.aptoide.pt.install_manager

import android.content.pm.PackageInfo
import androidx.core.content.pm.PackageInfoCompat
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.repository.PackageInfoRepository
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class RealApp private constructor(
  override val packageName: String,
  private val taskFactory: Task.Factory,
  private val packageInfoRepository: PackageInfoRepository,
  private val context: CoroutineContext,
) : App {

  private var _packageInfo: PackageInfo? = null

  private val _installedVersionCode get() = _packageInfo?.let(PackageInfoCompat::getLongVersionCode)

  override val packageInfo get() = _packageInfo

  override suspend fun getTask(): Task? = withContext(context) {
    taskFactory.getTask(packageName)
  }

  override suspend fun install(installPackageInfo: InstallPackageInfo): Task {
    return when {
      getTask() != null -> {
        throw IllegalStateException("Another task is already queued")
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
    if (getTask() != null) throw IllegalStateException("Another task is already queued")
    val version = _installedVersionCode ?: throw IllegalStateException("The $packageName is not installed")
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
    suspend fun create(
      packageName: String,
      packageInfo: PackageInfo?,
      taskFactory: Task.Factory,
      packageInfoRepository: PackageInfoRepository,
      context: CoroutineContext,
    ) = RealApp(
      packageName = packageName,
      taskFactory = taskFactory,
      packageInfoRepository = packageInfoRepository,
      context = context,
    ).apply {
      _packageInfo = packageInfo ?: packageInfoRepository.get(packageName)
    }
  }
}
