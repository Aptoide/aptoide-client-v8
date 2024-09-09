package cm.aptoide.pt.feature_apps.domain

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsRepository
import cm.aptoide.pt.feature_apps.presentation.toPackageNameParam
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppMetaUseCase @Inject constructor(private val appsRepository: AppsRepository) {

  suspend fun getMetaInfo(
    source: String,
    useStoreName: Boolean = true,
  ): App =
    appsRepository.getAppMeta(
      source = source,
      useStoreName = useStoreName && !source.contains("com.appcoins.wallet".toPackageNameParam())
    )
}
