package cm.aptoide.pt.feature_apps.domain

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppMetaUseCase @Inject constructor(private val appsRepository: AppsRepository) {

  suspend fun getMetaInfo(packageName: String): App =
    appsRepository.getMeta(packageName = packageName)
}
