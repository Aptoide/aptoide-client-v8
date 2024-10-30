package cm.aptoide.pt.feature_apps.domain

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppMetaUseCase @Inject constructor(private val appRepository: AppRepository) {
  suspend fun getMetaInfo(source: String): App = appRepository.getAppMeta(source = source)
}
