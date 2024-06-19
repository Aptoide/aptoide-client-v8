package cm.aptoide.pt.aptoide_network.data.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserAgentInterceptor @Inject constructor(private val provider: GetUserAgent) :
  Interceptor {

  override fun intercept(chain: Interceptor.Chain): Response {
    val userAgent = provider.invoke()
    val newRequest = chain.request().newBuilder().header("User-Agent", userAgent).build()
    return chain.proceed(newRequest)
  }
}

interface GetUserAgent{
  operator fun invoke(): String
}
