package cm.aptoide.pt.feature_home.data.deviceapi

import cm.aptoide.pt.feature_home.data.deviceapi.model.HomeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DeviceApiWidgetsService {

  @GET("android/v1/home")
  suspend fun getHome(
    @Query("variant") variant: String,
    @Query("refresh") refresh: Int? = null,
  ): HomeResponse
}
