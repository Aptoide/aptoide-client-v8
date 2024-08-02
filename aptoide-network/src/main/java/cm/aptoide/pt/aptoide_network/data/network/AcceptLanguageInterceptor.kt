package cm.aptoide.pt.aptoide_network.data.network

import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AcceptLanguageInterceptor @Inject constructor(private val provider: GetAcceptLanguage) : Interceptor {

  override fun intercept(chain: Chain): Response {
    val originalRequest = chain.request()
    val newRequest = originalRequest.newBuilder()
      .addHeader("Accept-Language", provider.getAcceptLanguage())
      .build()
    return chain.proceed(newRequest)
  }
}

interface GetAcceptLanguage {
  fun getAcceptLanguage(): String
}
