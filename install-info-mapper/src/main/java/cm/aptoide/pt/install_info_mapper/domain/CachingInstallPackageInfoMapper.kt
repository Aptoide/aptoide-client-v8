package cm.aptoide.pt.install_info_mapper.domain

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CachingInstallPackageInfoMapper(
  private val installPackageInfoMapper: InstallPackageInfoMapper,
) : InstallPackageInfoMapper {
  private val cache: MutableMap<String, InstallPackageInfo> = mutableMapOf()
  private val mutex = Mutex()

  override suspend fun map(app: App): InstallPackageInfo =
    mutex.withLock {
      cache["${app.packageName}${app.versionCode}"] ?: installPackageInfoMapper.map(app).also {
        cache["${app.packageName}${app.versionCode}"] = it
      }
    }
}
