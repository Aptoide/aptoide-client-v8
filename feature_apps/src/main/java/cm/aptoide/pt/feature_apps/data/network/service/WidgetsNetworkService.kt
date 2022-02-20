package cm.aptoide.pt.feature_apps.data.network.service

import cm.aptoide.pt.feature_apps.data.network.model.BaseV7DataListResponse
import cm.aptoide.pt.feature_apps.data.network.model.WidgetsJSON
import retrofit2.Response
import retrofit2.http.GET

internal class WidgetsNetworkService(private val widgetsRemoteDataSource: Retrofit) :
  WidgetsRemoteService {

  override suspend fun getStoreWidgets(): Response<BaseV7DataListResponse<WidgetsJSON.WidgetNetwork>> {
    return widgetsRemoteDataSource.getStoreWidgets()
  }

  internal interface Retrofit {
    @GET("getStoreWidgets?aptoide_vercode=20000&store_id=15")
    suspend fun getStoreWidgets(): Response<BaseV7DataListResponse<WidgetsJSON.WidgetNetwork>>
  }
}
