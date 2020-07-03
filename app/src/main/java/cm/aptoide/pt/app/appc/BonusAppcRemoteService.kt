package cm.aptoide.pt.app.appc

import retrofit2.http.GET
import rx.Observable
import rx.Scheduler
import rx.Single

class BonusAppcRemoteService(private val serviceApi: ServiceApi,
                             private val ioScheduler: Scheduler) : BonusAppcService {

  override fun getBonusAppc(): Single<BonusAppcModel> {
    return serviceApi.getAppcBonus()
        .map { response -> mapResponse(response) }
        .toSingle()
        .subscribeOn(ioScheduler)
        .onErrorReturn { mapErrorResponse() };
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
    fun getAppcBonus(): Observable<BonusAppcResponse>
  }


}