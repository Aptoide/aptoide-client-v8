package cm.aptoide.pt.feature_categories.data.network.service

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.feature_categories.data.network.model.CategoryJson
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal class CategoriesNetworkService(
  private val categoriesRemoteDataSource: Retrofit,
  private val storeName: String
): CategoriesRemoteService {

  override suspend fun getCategoriesList(query: String): BaseV7DataListResponse<CategoryJson> {
    return categoriesRemoteDataSource.getCategoriesList(query, storeName)
  }

  internal interface Retrofit {
    @GET("store/groups/get/{query}")
    suspend fun getCategoriesList(
      @Path(value = "query", encoded = true) path: String,
      @Query("store_name") storeName: String,
      @Query("aab") aab: Int = 1,
    ): BaseV7DataListResponse<CategoryJson>
  }
}
