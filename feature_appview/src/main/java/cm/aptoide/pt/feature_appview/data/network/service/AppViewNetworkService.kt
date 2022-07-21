package cm.aptoide.pt.feature_appview.data.network.service

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.feature_appview.data.network.RemoteAppViewRepository
import cm.aptoide.pt.feature_appview.data.network.model.RelatedCardJson
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

class AppViewNetworkService(private val appViewRemoteDataSource: Retrofit) :
  RemoteAppViewRepository {

  override suspend fun getRelatedContent(packageName: String): Response<BaseV7DataListResponse<RelatedCardJson>> {
    return appViewRemoteDataSource.getRelatedContent(packageName)
  }

  interface Retrofit {
    @GET("get/type=CURATION_1/aptoide_uid=0/limit=10")
    suspend fun getRelatedContent(
      @Query(value = "package_name", encoded = true) packageName: String,
    ): Response<BaseV7DataListResponse<RelatedCardJson>>
  }
}