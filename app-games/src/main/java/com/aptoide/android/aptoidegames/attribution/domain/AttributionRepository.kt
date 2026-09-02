package com.aptoide.android.aptoidegames.attribution.domain

interface AttributionRepository {
  suspend fun getAttribution(
    packageName: String,
    guestUid: String?,
    timestamp: Long,
    vercode: Long,
    installerPackageName: String?,
  ): AttributionModel
}
