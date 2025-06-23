package com.aptoide.android.aptoidegames.feature_rtb.repository

import retrofit2.http.Body
import retrofit2.http.POST

interface RTBApi {
  @POST("/ad_request")
  suspend fun getApps(@Body request: RTBRequest): List<RTBResponse>
}
