package com.aptoide.android.aptoidegames.feature_rtb.repository

import retrofit2.http.Body
import retrofit2.http.POST

interface RTBCollectorApi {
  @POST("/installed-packages")
  suspend fun sendInstalledPackages(@Body request: InstalledPackagesRequest)
}
