package cm.aptoide.pt.feature_search.data.network.service

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.aptoide_network.di.RetrofitV7
import cm.aptoide.pt.feature_apps.data.model.AppJSON
import cm.aptoide.pt.feature_search.data.network.RemoteSearchRepository
import cm.aptoide.pt.feature_search.domain.repository.SearchStoreManager
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRetrofitService @Inject constructor(
  @RetrofitV7 private val searchAppRetrofitService: SearchAppRetrofitService,
  private val searchStoreManager: SearchStoreManager,
) :
  RemoteSearchRepository {

  override suspend fun searchApp(keyword: String): Response<BaseV7DataListResponse<AppJSON>> {
    return if (searchStoreManager.shouldAddStore()) {
      searchAppRetrofitService.searchApp(keyword, 25, searchStoreManager.getStore())
    } else {
      searchAppRetrofitService.searchApp(keyword, 25, null)
    }
  }

  override suspend fun getTopSearchedApps(): Response<BaseV7DataListResponse<AppJSON>> {
    return searchAppRetrofitService.getPopularSearch(searchStoreManager.getStore())
  }

  interface SearchAppRetrofitService {
    @GET("listSearchApps")
    suspend fun searchApp(
      @Query(value = "query", encoded = true) query: String,
      @Query(value = "limit") limit: Int,
      @Query(value = "store_name") storeName: String? = null,
      @Query("aab") aab: Int = 1,
    ): Response<BaseV7DataListResponse<AppJSON>>

    @GET("listApps/group_name=popular-search")
    suspend fun getPopularSearch(
      @Query(value = "store_name") storeName: String? = null,
      @Query("aab") aab: Int = 1,
      @Query("nocache") nocache: Int = 1,
    ): Response<BaseV7DataListResponse<AppJSON>>
  }
}
