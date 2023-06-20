package cm.aptoide.pt.aptoide_network.data.network

import cm.aptoide.pt.aptoide_network.di.VersionCode
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response
import javax.inject.Inject

class VersionCodeInterceptor @Inject constructor(
  @VersionCode private val versionCode: Int
) : Interceptor {
  override fun intercept(chain: Chain): Response {
    val originalRequest = chain.request()
    val newUrl = originalRequest.url.newBuilder()
      .addQueryParameter("aptoide_vercode", versionCode.toString())
      .build()
    val newRequest = originalRequest.newBuilder().url(newUrl).build()
    return chain.proceed(newRequest)
  }
}
