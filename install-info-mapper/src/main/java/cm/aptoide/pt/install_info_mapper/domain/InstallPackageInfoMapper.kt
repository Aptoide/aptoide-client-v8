package cm.aptoide.pt.install_info_mapper.domain

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo

interface InstallPackageInfoMapper {
  suspend fun map(app: App) : InstallPackageInfo
}
