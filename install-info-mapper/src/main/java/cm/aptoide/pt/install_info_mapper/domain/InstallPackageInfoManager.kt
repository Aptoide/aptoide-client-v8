package cm.aptoide.pt.install_info_mapper.domain

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InstallPackageInfoManager @Inject constructor(
  private val installPackageInfoMapper: InstallPackageInfoMapper
) {
  private val cache: MutableMap<String, InstallPackageInfo> = mutableMapOf()
  private val mutex = Mutex()

  suspend fun get(app: App): InstallPackageInfo =
    mutex.withLock {
      cache["${app.packageName}${app.versionCode}"] ?: installPackageInfoMapper.map(app).also {
        cache["${app.packageName}${app.versionCode}"] = it
      }
    }
}
