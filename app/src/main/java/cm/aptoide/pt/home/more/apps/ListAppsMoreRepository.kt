package cm.aptoide.pt.home.more.apps

import android.content.SharedPreferences
import android.content.res.Resources
import android.view.WindowManager
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator
import cm.aptoide.pt.dataprovider.model.v7.ListApps
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody
import cm.aptoide.pt.dataprovider.ws.v7.GetEskillsAppsRequest
import cm.aptoide.pt.dataprovider.ws.v7.ListAppsRequest
import cm.aptoide.pt.store.StoreCredentialsProvider
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

  fun getApps(url: String?, refresh: Boolean): Observable<ListApps> {
    return ListAppsRequest.ofAction(url, storeCredentialsProvider.fromUrl(url), bodyInterceptor,
        okHttpClient, converterFactory, tokenInvalidator, sharedPreferences, resources,
        windowManager).observe(refresh)
  }

  fun getEskillsApps(url: String, refresh: Boolean): Observable<ListApps> {
    return GetEskillsAppsRequest(url,
      okHttpClient, converterFactory, bodyInterceptor, tokenInvalidator, sharedPreferences
    ).observe(refresh)
  }

  fun loadMoreApps(url: String?, refresh: Boolean, offset: Int): Observable<ListApps> {
    val request =
        ListAppsRequest.ofAction(url, storeCredentialsProvider.fromUrl(url), bodyInterceptor,
            okHttpClient, converterFactory, tokenInvalidator, sharedPreferences, resources,
            windowManager)
    request.body.offset = offset
    return request.observe(refresh)
  }


}