package cm.aptoide.pt.aptoide_network.data.network

import cm.aptoide.pt.aptoide_network.di.StoreDomain
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostCacheInterceptor @Inject constructor(@StoreDomain private val storeDomain: String) :
  Interceptor {

  override fun intercept(chain: Interceptor.Chain): Response {
    val currentRequest = chain.request()
    val builder = currentRequest.newBuilder()
    if (!currentRequest.method.equals("GET", ignoreCase = true)) {
      if (storeDomain.contains(currentRequest.url.host)) {
        val newHost = currentRequest.url.host.replace("-cache", "")
        val newHttpUrl = currentRequest.url.newBuilder().host(newHost).build()
        val newRequest = builder.url(newHttpUrl).build()
        return chain.proceed(newRequest)
      } else {
        return chain.proceed(currentRequest)
      }
    } else {
      return chain.proceed(currentRequest)
    }
  }
}
