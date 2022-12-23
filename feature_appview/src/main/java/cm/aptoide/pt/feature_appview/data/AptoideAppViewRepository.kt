package cm.aptoide.pt.feature_appview.data

import cm.aptoide.pt.aptoide_network.di.RetrofitV7ActionItem
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppsRepository
import cm.aptoide.pt.feature_appview.data.network.RemoteAppViewRepository
import cm.aptoide.pt.feature_appview.data.network.model.RelatedCardJson
import cm.aptoide.pt.feature_appview.domain.model.Appearance
import cm.aptoide.pt.feature_appview.domain.model.Caption
import cm.aptoide.pt.feature_appview.domain.model.RelatedCard
import cm.aptoide.pt.feature_appview.domain.repository.AppViewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideAppViewRepository @Inject constructor(
  private val appsRepository: AppsRepository,
  @RetrofitV7ActionItem private val remoteAppViewRepository: RemoteAppViewRepository,
) :
  AppViewRepository {

  override fun getAppInfo(packageName: String): Flow<App> =
    appsRepository.getApp(packageName = packageName)

  override fun getSimilarApps(packageName: String): Flow<List<App>> =
    appsRepository.getRecommended("package_name=$packageName")

  override fun getAppcSimilarApps(packageName: String): Flow<List<App>> =
    appsRepository.getRecommended("package_name=$packageName/section=appc")

  override fun getOtherVersions(packageName: String): Flow<List<App>> =
    appsRepository.getAppVersions(packageName)

  override fun getRelatedContent(packageName: String): Flow<List<RelatedCard>> {
    return flow {
      val response = remoteAppViewRepository.getRelatedContent(packageName)
        .datalist?.list?.map { it.toDomainModel() }
        ?: throw IllegalStateException()
      emit(response)
    }
  }

  private fun RelatedCardJson.toDomainModel(): RelatedCard {
    return RelatedCard(
      id = this.id,
      type = this.type,
      subType = this.subtype,
      flair = this.flair,
      title = this.title,
      slug = this.slug,
      caption = this.caption,
      summary = this.summary,
      icon = this.icon,
      url = this.url,
      views = this.views,
      appearance = Appearance(Caption(this.appearance.caption.theme)),
      date = this.date,
    )
  }
}
