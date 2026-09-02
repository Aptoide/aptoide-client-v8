package com.aptoide.android.aptoidegames.attribution.data

import com.aptoide.android.aptoidegames.attribution.domain.AttributionModel
import com.aptoide.android.aptoidegames.attribution.domain.AttributionRepository
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

internal class AptoideAttributionRepository @Inject constructor(
  private val mmpRemoteDataSource: Service,
) : AttributionRepository {

  override suspend fun getAttribution(
    packageName: String,
    guestUid: String?,
    timestamp: Long,
    vercode: Long,
    installerPackageName: String?,
  ): AttributionModel = mmpRemoteDataSource.getAttribution(
    packageName = packageName,
    guestUid = guestUid,
    timestamp = timestamp,
    vercode = vercode,
    installerPackageName = installerPackageName,
  ).toDomainModel()

  interface Service {
    @GET("attribution")
    suspend fun getAttribution(
      @Query("package_name") packageName: String,
      @Query("guest_uid") guestUid: String?,
      @Query("timestamp") timestamp: Long,
      @Query("vercode") vercode: Long,
      @Query("installer_package_name") installerPackageName: String?,
    ): AttributionJSON
  }
}

fun AttributionJSON.toDomainModel(): AttributionModel = AttributionModel(
  packageName = packageName,
  oemId = oemId,
  guestUid = guestUid.orEmpty(),
  utmSource = utmSource,
  utmMedium = utmMedium,
  utmCampaign = utmCampaign,
  utmTerm = utmTerm,
  utmContent = utmContent,
)
