package cm.aptoide.pt.feature_appview.data

import cm.aptoide.pt.aptoide_network.di.RetrofitV7ActionItem
import cm.aptoide.pt.feature_apps.data.AppResult
import cm.aptoide.pt.feature_apps.data.AppsRepository
import cm.aptoide.pt.feature_apps.data.AppsResult
import cm.aptoide.pt.feature_appview.data.network.RemoteAppViewRepository
import cm.aptoide.pt.feature_appview.data.network.model.RelatedCardJson
import cm.aptoide.pt.feature_appview.domain.model.Appearance
import cm.aptoide.pt.feature_appview.domain.model.Caption
import cm.aptoide.pt.feature_appview.domain.model.RelatedCard
import cm.aptoide.pt.feature_appview.domain.repository.*
import cm.aptoide.pt.feature_reactions.ReactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideAppViewRepository @Inject constructor(
  private val appsRepository: AppsRepository,
  @RetrofitV7ActionItem private val remoteAppViewRepository: RemoteAppViewRepository,
  private val reactionsRepository: ReactionsRepository
) :
  AppViewRepository {

  override fun getAppInfo(packageName: String): Flow<AppViewResult> {
    return appsRepository.getApp(packageName = packageName).map {
      when (it) {
        is AppResult.Success -> {
          AppViewResult.Success(it.data)
        }
        is AppResult.Error -> {
          AppViewResult.Error(it.e)
        }
      }
    }
  }

  override fun getSimilarApps(packageName: String): Flow<SimilarAppsResult> {
    return appsRepository.getRecommended("package_name=$packageName").map {
      when (it) {
        is AppsResult.Success -> {
          return@map SimilarAppsResult.Success(it.data)
        }
        is AppsResult.Error -> {
          return@map SimilarAppsResult.Error(it.e)
        }
      }
    }

  }

  override fun getAppcSimilarApps(packageName: String): Flow<SimilarAppsResult> {
    return appsRepository.getRecommended("package_name=$packageName/section=appc")
      .map { appcResult ->
        when (appcResult) {
          is AppsResult.Success -> {
            return@map SimilarAppsResult.Success(appcResult.data)

          }
          is AppsResult.Error -> {
            return@map SimilarAppsResult.Error(appcResult.e)
          }
        }
      }
  }

  override fun getOtherVersions(packageName: String): Flow<OtherVersionsResult> {
    return appsRepository.getAppVersions(packageName).map {
      when (it) {
        is AppsResult.Success -> {
          OtherVersionsResult.Success(it.data)
        }
        is AppsResult.Error -> {
          OtherVersionsResult.Error(it.e)
        }
      }
    }
  }

  override fun getRelatedContent(packageName: String): Flow<RelatedContentResult> {
    return flow {
      val relatedContentResponse = remoteAppViewRepository.getRelatedContent(packageName)
      if (relatedContentResponse.isSuccessful) {
        relatedContentResponse.body()?.datalist?.list?.let {
          emit(RelatedContentResult.Success(it.map { relatedCardJson -> relatedCardJson.toDomainModel() }))
        }
      } else {
        emit(RelatedContentResult.Error(IllegalStateException()))
      }
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
      date = this.date, reactionsNumber = 500
    )
  }
}