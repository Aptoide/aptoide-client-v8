package cm.aptoide.pt.autoupdate

import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException
import retrofit2.http.GET
import retrofit2.http.Path
import rx.Observable
import rx.Single

class AutoUpdateService(private val service: Service, private val packageName: String,
                        private val clientSdkVersion: Int) {

  private var loading = false

  fun loadAutoUpdateModel(): Single<AutoUpdateModel> {
    if (loading) {
      return Single.just(AutoUpdateModel(loading = true))
    }
    return service.getAutoUpdateResponse(packageName, clientSdkVersion)
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
  @GET("apks/package/autoupdate/get/package_name={package_name}/sdk={client_sdk_version}")
  fun getAutoUpdateResponse(
      @Path(value = "package_name") packageName: String, @Path(value = "client_sdk_version")
      clientSdkVersion: Int): Observable<AutoUpdateJsonResponse>
}