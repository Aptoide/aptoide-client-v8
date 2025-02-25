package cm.aptoide.pt.appcomingsoon.repository

import cm.aptoide.pt.appcomingsoon.domain.AppComingSoonCard
import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AptoideAppComingSoonPromotionalRepository @Inject constructor(
  private val appComingSoonRemoteDataSource: Retrofit,
) :
  AppComingSoonPromotionalRepository {

  override suspend fun getAppComingSoonCard(url: String): AppComingSoonCard {
    if (url.contains("cards/")) {
      return appComingSoonRemoteDataSource.getCard(url.split("cards/")[1])
        .datalist
        ?.list
        .takeIf { !it.isNullOrEmpty() }
        ?.get(0)
        ?.toDomainModel()
        ?: throw IllegalStateException("AppComingSoon Card can not be empty")
    } else {
      throw IllegalStateException("Invalid URL while trying to load AppComingSoon card")
    }
  }
}

internal interface Retrofit {
  @GET("cards/{widgetUrl}")
  suspend fun getCard(
    @Path("widgetUrl", encoded = true) widgetUrl: String,
  ): BaseV7DataListResponse<AppComingSoonCardJson>
}

private fun AppComingSoonCardJson.toDomainModel(): AppComingSoonCard {
  return AppComingSoonCard(
    id = this.id,
    caption = this.caption,
    title = this.title,
    packageName = this.packageName,
    featureGraphic = this.graphic
  )
}
