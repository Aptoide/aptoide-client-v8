package cm.aptoide.pt.feature_apps.data.network.service

import cm.aptoide.pt.feature_apps.data.network.model.BaseV7DataListResponse
import cm.aptoide.pt.feature_apps.data.network.model.WidgetsJSON
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

internal class WidgetsNetworkService @Inject constructor(
  private val widgetsRemoteDataSource: Retrofit,
  private val storeName: String
) :
  WidgetsRemoteService {

  override suspend fun getStoreWidgets(): Response<BaseV7DataListResponse<WidgetsJSON.WidgetNetwork>> {
    return widgetsRemoteDataSource.getStoreWidgets(storeName)
  }

  internal interface Retrofit {
    @GET("getStoreWidgets?aptoide_vercode=20000&limit=25")
    suspend fun getStoreWidgets(
      @Query("store_name") storeName: String
    ): Response<BaseV7DataListResponse<WidgetsJSON.WidgetNetwork>>
  }
}
