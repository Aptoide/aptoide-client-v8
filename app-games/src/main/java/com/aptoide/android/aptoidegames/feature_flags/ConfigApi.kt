package com.aptoide.android.aptoidegames.feature_flags

import androidx.annotation.Keep
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * The remote configuration of the device API. The server resolves every condition (country,
 * version range, percentile) and answers with the final values for this install. The country is
 * left out on purpose: the edge derives it from the connection.
 */
interface ConfigApi {

  @GET("android/v1/config")
  suspend fun getConfig(
    @Query("variant") variant: String,
    @Query("version_code") versionCode: Int,
    @Query("bucket_id") bucketId: String,
  ): ConfigResponse
}

@Keep
data class ConfigResponse(
  @SerializedName("template_version") val templateVersion: Int?,
  // A flat key -> string map; kept as raw JSON so an unexpected value type cannot fail the parse
  @SerializedName("values") val values: JsonObject?,
)
