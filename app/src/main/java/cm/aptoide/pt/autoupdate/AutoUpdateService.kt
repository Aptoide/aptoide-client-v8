package cm.aptoide.pt.autoupdate

import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException
import retrofit2.http.GET
import retrofit2.http.Path
import rx.Observable
import rx.Single

class AutoUpdateService(private val service: Service, private val packageName: String,
                        private val autoUpdateStoreName: String) {

  private var loading = false

  fun loadAutoUpdateModel(): Single<AutoUpdateModel> {
    if (loading) {
      return Single.just(AutoUpdateModel(loading = true))
    }
    return service.getJsonResponse(autoUpdateStoreName)
        .doOnSubscribe { loading = true }
        .doOnUnsubscribe { loading = false }
        .doOnTerminate { loading = false }
        .flatMap {
          Observable.just(AutoUpdateModel(it.versioncode, it.uri, it.md5, it.minSdk, packageName))
        }
        .onErrorReturn { createErrorAutoUpdateModel(it) }
        .toSingle()

  }

  private fun createErrorAutoUpdateModel(throwable: Throwable?): AutoUpdateModel? {
    return when (throwable) {
      is NoNetworkConnectionException -> AutoUpdateModel(status = Status.ERROR_NETWORK)
      else -> AutoUpdateModel(status = Status.ERROR_GENERIC)
    }
  }
}

interface Service {
  @GET("latest_version_{storeName}.json")
  fun getJsonResponse(
      @Path(value = "storeName") storeName: String): Observable<AutoUpdateJsonResponse>
}