package cm.aptoide.pt.feature_apps.data.deviceapi

import cm.aptoide.pt.feature_apps.data.deviceapi.model.AppResponse
import cm.aptoide.pt.feature_apps.data.deviceapi.model.AppSummaryPageResponse
import cm.aptoide.pt.feature_apps.data.deviceapi.model.RelatedAppsResponse
import cm.aptoide.pt.feature_apps.data.deviceapi.model.ReleaseHistoryResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Device-API app routes (`device.openapi.json`). Device-profile params ride as
 * clean query params (never v7's `q=` blob); `variant` only where the spec
 * carries it (search/browse). Null query args are omitted by Retrofit — fail-open.
 */
interface DeviceApiAppsService {

  @GET("android/v1/apps/{package_name}")
  suspend fun getApp(
    @Path("package_name") packageName: String,
    @Query("sdk") sdk: Int?,
    @Query("abi") abi: String?,
    @Query("tv") tv: Boolean?,
    @Query("density") density: Int?,
  ): AppResponse

  @GET("android/v1/apps")
  suspend fun searchOrBrowse(
    @Query("q") q: String? = null,
    @Query("category") category: String? = null,
    @Query("sort") sort: String? = null,
    @Query("cursor") cursor: String? = null,
    @Query("limit") limit: Int? = null,
    @Query("variant") variant: String,
    @Query("sdk") sdk: Int? = null,
    @Query("abi") abi: String? = null,
    @Query("tv") tv: Boolean? = null,
    @Query("density") density: Int? = null,
    @Query("refresh") refresh: Int? = null,
  ): AppSummaryPageResponse

  @GET("android/v1/apps/{package_name}/related")
  suspend fun getRelated(
    @Path("package_name") packageName: String,
    @Query("limit") limit: Int? = null,
    @Query("sdk") sdk: Int?,
    @Query("abi") abi: String?,
    @Query("tv") tv: Boolean?,
    @Query("density") density: Int?,
  ): RelatedAppsResponse

  @GET("android/v1/apps/{package_name}/releases")
  suspend fun getReleases(
    @Path("package_name") packageName: String,
    @Query("cursor") cursor: String? = null,
    @Query("limit") limit: Int? = null,
    @Query("sdk") sdk: Int?,
    @Query("abi") abi: String?,
    @Query("tv") tv: Boolean?,
    @Query("density") density: Int?,
  ): ReleaseHistoryResponse
}
