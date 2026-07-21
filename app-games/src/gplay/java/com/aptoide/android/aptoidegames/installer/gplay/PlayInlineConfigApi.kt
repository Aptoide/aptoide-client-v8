package com.aptoide.android.aptoidegames.installer.gplay

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path

interface PlayInlineConfigApi {

  @GET("android/v1/config/play-inline/{packageName}")
  suspend fun getPlayInlineConfig(
    @Path("packageName") packageName: String,
  ): PlayInlineConfigResponse
}

data class PlayInlineConfigResponse(
  @SerializedName("catalog_token") val catalogToken: String?,
  // Kept fresh by the backend (re-ingested before the 7 day validity runs out),
  // so it is not checked client-side
  @SerializedName("expires_at") val expiresAt: String?,
)
