package cm.aptoide.pt.apkfy

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.http.GET

class AptoideApkfyService(private val serviceApi: ServiceApi) : ApkfyService {

  override suspend fun getApkfy(): ApkfyModel {
    return withContext(Dispatchers.IO) {
      val apkfyResponse = serviceApi.getApkfyModel()
      val apkfyResponseBody = apkfyResponse.body()
      if (apkfyResponse.isSuccessful && apkfyResponseBody != null) {
        return@withContext apkfyResponseBody.mapToApkfyModel()
      } else {
        return@withContext mapErrorResponse()
      }
    }
  }

  private fun mapErrorResponse(): ApkfyModel {
    return ApkfyModel("", null, "", "")
  }

  interface ServiceApi {
    @GET("apkfy")
    suspend fun getApkfyModel(): Response<ApkfyResponse>
  }
}
