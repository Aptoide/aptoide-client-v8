package cm.aptoide.pt.feature_apps.domain

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppRepository
import cm.aptoide.pt.feature_apps.data.SplitsRepository
import cm.aptoide.pt.feature_apps.data.model.DynamicSplitJSON
import cm.aptoide.pt.feature_apps.data.toDomainModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppMetaUseCase @Inject constructor(
  private val appRepository: AppRepository,
  private val splitsRepository: SplitsRepository,
) {
  suspend fun getMetaInfo(source: String): App = appRepository.getAppMeta(source = source)
    .copy(hasMeta = true)
    .run {
      aab
        ?.let {
          val dSplits = splitsRepository.getAppsDynamicSplits(md5)
          it.copy(dynamicSplits = dSplits.map(DynamicSplitJSON::toDomainModel))
        }
        ?.let { copy(aab = it) }
        ?: this
    }
}
