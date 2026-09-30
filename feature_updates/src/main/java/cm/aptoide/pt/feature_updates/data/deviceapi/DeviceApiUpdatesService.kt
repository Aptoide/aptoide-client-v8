package cm.aptoide.pt.feature_updates.data.deviceapi

import androidx.annotation.Keep
import cm.aptoide.pt.feature_apps.data.deviceapi.model.AppResponse
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Which of the installed apps have an update, with the update's details. Under a curated
 * catalog an app the catalog does not hold reads as no update, so [variant] is left out to
 * ask about every installed app.
 */
interface DeviceApiUpdatesService {

  @POST("android/v1/apps/updates")
  suspend fun getUpdates(
    @Query("variant") variant: String?,
    @Body body: UpdatesRequestBody,
  ): UpdatesResponse
}

@Keep
data class UpdatesRequestBody(
  @SerializedName("apps") val apps: List<InstalledAppRequest>,
  @SerializedName("device") val device: DeviceRequest?,
)

@Keep
data class InstalledAppRequest(
  @SerializedName("package_name") val packageName: String,
  @SerializedName("version_code") val versionCode: Long,
  /** Forty hex characters, no separators. */
  @SerializedName("signer_sha1") val signerSha1: String,
)

@Keep
data class DeviceRequest(
  @SerializedName("sdk") val sdk: Int?,
  @SerializedName("abis") val abis: List<String>?,
  @SerializedName("tv") val tv: Boolean?,
  @SerializedName("density") val density: Int?,
)

@Keep
data class UpdatesResponse(
  @SerializedName("results") val results: List<UpdateVerdictResponse>? = null,
)

@Keep
data class UpdateVerdictResponse(
  @SerializedName("package_name") val packageName: String? = null,
  /** `update_available` or `no_update`. */
  @SerializedName("status") val status: String? = null,
  @SerializedName("update") val update: AppResponse? = null,
)
