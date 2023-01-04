package cm.aptoide.pt.apps

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.feature_home.data.network.model.WidgetsJSON
import cm.aptoide.pt.feature_home.data.network.service.WidgetsRemoteService
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

class WidgetsNetworkService @Inject constructor(
  private val widgetsRemoteDataSource: Retrofit,
  private val storeName: String
) :
  WidgetsRemoteService {

  override suspend fun getStoreWidgets(): BaseV7DataListResponse<WidgetsJSON.WidgetNetwork> {
    return widgetsRemoteDataSource.getStoreWidgets(storeName)
  }

  interface Retrofit {
    @GET("getStoreWidgets?aptoide_vercode=20000&limit=25")
    suspend fun getStoreWidgets(
      @Query("store_name") storeName: String,
      @Query("aab") aab: Int = 1
    ): BaseV7DataListResponse<WidgetsJSON.WidgetNetwork>
  }
}