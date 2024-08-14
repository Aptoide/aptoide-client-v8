package cm.aptoide.pt.feature_apps.domain

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsRepository
import cm.aptoide.pt.feature_apps.presentation.toAppIdParam
import cm.aptoide.pt.feature_apps.presentation.toPackageNameParam
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppMetaUseCase @Inject constructor(private val appsRepository: AppsRepository) {

  suspend fun getMetaInfoByAppId(
    appId: Long,
    useStoreName: Boolean = true,
  ): App =
    appsRepository.getMetaBySource(
      source = appId.toAppIdParam(),
      useStoreName = useStoreName
    )

  suspend fun getMetaInfoBySource(
    source: String,
    useStoreName: Boolean = true,
  ): App =
    appsRepository.getMetaBySource(
      source = source,
      useStoreName = useStoreName && !source.contains("com.appcoins.wallet".toPackageNameParam())
    )
}
