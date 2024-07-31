package cm.aptoide.pt.feature_apps.domain

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsRepository
import cm.aptoide.pt.feature_apps.presentation.toPackageNameParam
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppMetaUseCase @Inject constructor(private val appsRepository: AppsRepository) {

  suspend fun getMetaInfo(packageName: String): App =
    appsRepository.getMetaBySource(source = packageName.toPackageNameParam())

  suspend fun getMetaInfoBySource(source: String): App =
    appsRepository.getMetaBySource(source = source)
}
