package cm.aptoide.pt.aab

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

class DynamicSplitsRemoteService(private val dynamicSplitsApi: DynamicSplitsApi,
                                 private val mapper: DynamicSplitsMapper) :
    DynamicSplitsService {

  override suspend fun getDynamicSplitsByMd5(apkMd5Sum: String): DynamicSplitsModel {

    return withContext(Dispatchers.IO) {

      val dynamicSplitsResponse = dynamicSplitsApi.getDynamicSplitsByMd5(apkMd5Sum)
      val dynamicSplitsResponseBody = dynamicSplitsResponse.body()
      if (dynamicSplitsResponse.isSuccessful && dynamicSplitsResponseBody != null) {
        return@withContext mapResponse(dynamicSplitsResponseBody)
      } else {
        return@withContext mapErrorResponse()
      }
    }
  }

  private fun mapErrorResponse(): DynamicSplitsModel {
    return DynamicSplitsModel(Collections.emptyList())
  }

  private fun mapResponse(dynamicSplitsResponseBody: DynamicSplitsResponse): DynamicSplitsModel {

    return DynamicSplitsModel(mapper.mapDynamicSplits(dynamicSplitsResponseBody.dynamicSplitList))
  }

  interface DynamicSplitsApi {
    @GET("app/getDynamicSplits")
    suspend fun getDynamicSplitsByMd5(
        @Query("apk_md5sum") md5: String): Response<DynamicSplitsResponse>
  }

}