package cm.aptoide.pt.home.more.apps

import android.content.SharedPreferences
import android.content.res.Resources
import android.view.WindowManager
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator
import cm.aptoide.pt.dataprovider.model.v7.ListApps
import cm.aptoide.pt.dataprovider.model.v7.listapp.App
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody
import cm.aptoide.pt.dataprovider.ws.v7.ListAppsRequest
import cm.aptoide.pt.store.StoreCredentialsProvider
import cm.aptoide.pt.view.app.Application
import okhttp3.OkHttpClient
import retrofit2.Converter
import rx.Observable

class ListAppsMoreRepository(val storeCredentialsProvider: StoreCredentialsProvider,
                             val bodyInterceptor: BodyInterceptor<BaseBody>,
                             val okHttpClient: OkHttpClient,
                             val converterFactory: Converter.Factory,
                             val tokenInvalidator: TokenInvalidator,
                             val sharedPreferences: SharedPreferences,
                             val resources: Resources,
                             val windowManager: WindowManager) {

  private var offset = 0
  private var total = 0
  private var limit = 0

  fun getApps(url: String?, refresh: Boolean): Observable<List<Application>> {
    return ListAppsRequest.ofAction(url, storeCredentialsProvider.fromUrl(url), bodyInterceptor,
        okHttpClient, converterFactory, tokenInvalidator, sharedPreferences, resources,
        windowManager).observe(refresh).map { response -> mapResponse(response) }
  }

  fun loadMoreApps(url: String?, refresh: Boolean): Observable<List<Application>> {
    if (offset >= total)
      return Observable.just(null)
    else {
      val request =
          ListAppsRequest.ofAction(url, storeCredentialsProvider.fromUrl(url), bodyInterceptor,
              okHttpClient, converterFactory, tokenInvalidator, sharedPreferences, resources,
              windowManager)
      request.body.offset = limit
      return request.observe(refresh).map { response -> mapResponse(response) }
    }
  }

  private fun mapResponse(listApps: ListApps): List<Application> {
    val result = ArrayList<Application>()
    offset = listApps.dataList.offset
    total = listApps.dataList.total
    limit = listApps.dataList.limit
    for (app: App in listApps.dataList.list) {
      result.add(Application(app.name, app.icon, app.stats.rating.avg, app.stats.downloads,
          app.packageName,
          app.id, "", app.appcoins != null && app.appcoins.hasBilling()))
    }
    return result
  }
}