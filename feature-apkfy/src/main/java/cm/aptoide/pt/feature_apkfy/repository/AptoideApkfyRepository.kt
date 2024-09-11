package cm.aptoide.pt.feature_apkfy.repository

import cm.aptoide.pt.feature_apkfy.domain.ApkfyModel
import retrofit2.http.GET
import javax.inject.Inject

internal class AptoideApkfyRepository @Inject constructor(
  private val mmpRemoteDataSource: Retrofit,
) : ApkfyRepository {

  override suspend fun getApkfy() = mmpRemoteDataSource.getApkfy().toDomainModel()

  interface Retrofit {
    @GET("apkfy")
    suspend fun getApkfy(): ApkfyJSON
  }
}

fun ApkfyJSON.toDomainModel(): ApkfyModel = ApkfyModel(
  packageName = packageName,
  appId = appId,
  oemId = oemId,
  guestUid = guestUId,
  utmSource = utmSource,
  utmMedium = utmMedium,
  utmCampaign = utmCampaign,
  utmTerm = utmTerm,
  utmContent = utmContent
)
