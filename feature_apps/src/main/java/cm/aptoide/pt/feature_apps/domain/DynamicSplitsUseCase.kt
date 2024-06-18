package cm.aptoide.pt.feature_apps.domain

import cm.aptoide.pt.feature_apps.data.AppsRepository
import cm.aptoide.pt.feature_apps.data.DynamicSplit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DynamicSplitsUseCase @Inject constructor(
  private val appsRepository: AppsRepository,
) {

  suspend fun getDynamicSplits(md5: String): List<DynamicSplit> =
    appsRepository.getAppsDynamicSplits(md5 = md5)
}
