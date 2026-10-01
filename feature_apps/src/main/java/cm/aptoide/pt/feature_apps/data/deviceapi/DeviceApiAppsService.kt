package cm.aptoide.pt.feature_apps.data.deviceapi

import cm.aptoide.pt.feature_apps.data.deviceapi.model.AppResponse
import cm.aptoide.pt.feature_apps.data.deviceapi.model.AppsPageResponse
import cm.aptoide.pt.feature_apps.data.deviceapi.model.RelatedAppsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * The app routes of the device API. The device profile goes as plain query parameters, and
 * Retrofit leaves a null one out - the service fails open on anything it is not told.
 */
interface DeviceApiAppsService {

  @GET("android/v1/apps/{package_name}")
  suspend fun getApp(
    @Path("package_name") packageName: String,
    @Query("variant") variant: String,
    @Query("sdk") sdk: Int?,
    @Query("abi") abi: String?,
    @Query("tv") tv: Boolean?,
    @Query("density") density: Int?,
  ): AppResponse

  /** A search when [q] is given, a listing when [category] is. The service rejects both. */
  @GET("android/v1/apps")
  suspend fun getApps(
    @Query("q") q: String?,
    @Query("category") category: String?,
    @Query("sort") sort: String?,
    @Query("limit") limit: Int?,
    @Query("variant") variant: String,
    @Query("sdk") sdk: Int?,
    @Query("abi") abi: String?,
    @Query("tv") tv: Boolean?,
    @Query("density") density: Int?,
    @Query("refresh") refresh: Int?,
  ): AppsPageResponse

  @GET("android/v1/apps/{package_name}/related")
  suspend fun getRelated(
    @Path("package_name") packageName: String,
    @Query("limit") limit: Int?,
    @Query("variant") variant: String,
    @Query("sdk") sdk: Int?,
    @Query("abi") abi: String?,
    @Query("tv") tv: Boolean?,
    @Query("density") density: Int?,
  ): RelatedAppsResponse
}
