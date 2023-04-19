package cm.aptoide.pt.feature_editorial.data.network.service

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.feature_editorial.data.network.EditorialDetailJson
import cm.aptoide.pt.feature_editorial.data.network.EditorialRemoteService
import cm.aptoide.pt.feature_editorial.data.network.model.EditorialJson
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

class EditorialNetworkService(
  private val editorialRemoteDataSource: Retrofit,
  private val storeName: String
) :
  EditorialRemoteService {

  override suspend fun getLatestEditorial(): BaseV7DataListResponse<EditorialJson> {
    return editorialRemoteDataSource.getLatestEditorial()
  }

  override suspend fun getArticlesMeta(
    widgetUrl: String,
    subtype: String?
  ): BaseV7DataListResponse<EditorialJson> {
    return editorialRemoteDataSource.getArticlesMeta(widgetUrl, subtype)
  }

  override suspend fun getEditorialDetail(widgetUrl: String): EditorialDetailJson {
    return editorialRemoteDataSource.getArticle(widgetUrl)
  }

  override suspend fun getRelatedContent(packageName: String): BaseV7DataListResponse<EditorialJson> {
    return editorialRemoteDataSource.getRelatedArticlesMeta(packageName, storeName)
  }

  interface Retrofit {
    @GET("cards/get/type=CURATION_1/aptoide_uid=0/limit=1")
    suspend fun getLatestEditorial(
      @Query("aab") aab: Int = 1
    ): BaseV7DataListResponse<EditorialJson>

    @GET("cards/{widgetUrl}/aptoide_uid=0/")
    suspend fun getArticlesMeta(
      @Path("widgetUrl", encoded = true) widgetUrl: String,
      @Query("subtype") subtype: String?,
      @Query("aab") aab: Int = 1
    ): BaseV7DataListResponse<EditorialJson>

    @GET("card/{widgetUrl}/aptoide_uid=0/")
    suspend fun getArticle(
      @Path("widgetUrl", encoded = true) widgetUrl: String,
      @Query("aab") aab: Int = 1
    ): EditorialDetailJson

    @GET("cards/get/type=CURATION_1/aptoide_uid=0/limit=10")
    suspend fun getRelatedArticlesMeta(
      @Query(value = "package_name", encoded = true) packageName: String,
      @Query("store_name") storeName: String,
      @Query("aab") aab: Int = 1
    ): BaseV7DataListResponse<EditorialJson>
  }
}
