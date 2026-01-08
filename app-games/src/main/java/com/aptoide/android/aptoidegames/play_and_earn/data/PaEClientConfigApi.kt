package com.aptoide.android.aptoidegames.play_and_earn.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET

interface PaEClientConfigApi {

  @GET("/api/client/config")
  suspend fun getClientConfig(): ClientConfigResponse
}

@Keep
data class ClientConfigResponse(
  @SerializedName("heartbeat_interval_seconds") val heartbeatIntervalSeconds: Int
)
