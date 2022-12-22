package cm.aptoide.pt.feature_editorial.data.network.service

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.feature_editorial.data.network.EditorialDetailJson
import cm.aptoide.pt.feature_editorial.data.network.EditorialRemoteService
import cm.aptoide.pt.feature_editorial.data.network.model.EditorialJson
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

class EditorialNetworkService(private val editorialRemoteDataSource: Retrofit) :
  EditorialRemoteService {

  override suspend fun getLatestEditorial(): Response<BaseV7DataListResponse<EditorialJson>> {
    return editorialRemoteDataSource.getLatestEditorial()
  }

  override suspend fun getArticleMeta(
    widgetUrl: String,
    subtype: String?
  ): Response<BaseV7DataListResponse<EditorialJson>> {
    return editorialRemoteDataSource.getArticleMeta(widgetUrl, subtype)
  }

  override suspend fun getEditorialDetail(articleId: String): Response<EditorialDetailJson> {
    return editorialRemoteDataSource.getArticleDetail(articleId)
  }

  interface Retrofit {
    @GET("cards/get/type=CURATION_1/aptoide_uid=0/limit=1")
    suspend fun getLatestEditorial(
      @Query("aab") aab: Int = 1
    ): Response<BaseV7DataListResponse<EditorialJson>>

    @GET("cards/{widgetUrl}/aptoide_uid=0/")
    suspend fun getArticleMeta(
      @Path("widgetUrl", encoded = true) widgetUrl: String,
      @Query("subtype") subtype: String?,
      @Query("aab") aab: Int = 1
    ): Response<BaseV7DataListResponse<EditorialJson>>

    @GET("card/get/type=CURATION_1/id={id}/aptoide_uid=0/aab=true")
    suspend fun getArticleDetail(
      @Path("id", encoded = true) articleId: String,
      @Query("aab") aab: Int = 1
    ): Response<EditorialDetailJson>
  }
}
