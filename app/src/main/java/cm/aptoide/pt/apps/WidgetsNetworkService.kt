package cm.aptoide.pt.apps

import cm.aptoide.pt.aptoide_network.data.network.CacheConstants.CACHE_CONTROL_HEADER
import cm.aptoide.pt.aptoide_network.data.network.CacheConstants.NO_CACHE
import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.feature_home.data.network.model.WidgetsJSON
import cm.aptoide.pt.feature_home.data.network.service.WidgetsRemoteService
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import javax.inject.Inject

class WidgetsNetworkService @Inject constructor(
  private val widgetsRemoteDataSource: Retrofit,
  private val storeName: String,
  private val versionCode: Int
) :
  WidgetsRemoteService {

  override suspend fun getStoreWidgets(bypassCache: Boolean): BaseV7DataListResponse<WidgetsJSON.WidgetNetwork> {
    return widgetsRemoteDataSource.getStoreWidgets(
      storeName = storeName,
      bypassCache = if (bypassCache) NO_CACHE else null,
      versionCode = versionCode
    )
  }

  interface Retrofit {
    @GET("getStoreWidgets?limit=25")
    suspend fun getStoreWidgets(
      @Query("store_name") storeName: String,
      @Query("aab") aab: Int = 1,
      @Query("aptoide_vercode") versionCode: Int,
      @Header(CACHE_CONTROL_HEADER) bypassCache: String?,
    ): BaseV7DataListResponse<WidgetsJSON.WidgetNetwork>
  }
}
