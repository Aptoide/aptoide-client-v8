package cm.aptoide.pt.bonus

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.http.GET


class BonusAppcRemoteService(private val serviceApi: ServiceApi) : BonusAppcService {

  override suspend fun getBonusAppc(): BonusAppcModel {

    return withContext(Dispatchers.IO) {
      val bonusResponse = serviceApi.getAppcBonus()
      val bonusResponseBody = bonusResponse.body()
      if (bonusResponse.isSuccessful && bonusResponseBody != null) {
        return@withContext mapResponse(bonusResponseBody)
      } else {
        return@withContext mapErrorResponse()
      }
    }
  }

  private fun mapErrorResponse(): BonusAppcModel {
    return BonusAppcModel(false, 0)
  }

  private fun mapResponse(response: BonusAppcResponse): BonusAppcModel {
    if (response.status.equals("ACTIVE") && response.result.isNotEmpty()) {
      return BonusAppcModel(true, response.result.last().bonus)
    }
    return mapErrorResponse()
  }

  interface ServiceApi {
    @GET("gamification/levels")
    suspend fun getAppcBonus(): Response<BonusAppcResponse>
  }


}