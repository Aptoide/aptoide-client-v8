package cm.aptoide.pt.network

import cm.aptoide.pt.aptoide_network.data.network.UserAgentInterceptor
import okhttp3.Interceptor
import okhttp3.Response

class AptoideUserAgentInterceptor : UserAgentInterceptor {
  override fun buildUserAgent(): String {
    return "user-agent"
  }

  override fun intercept(chain: Interceptor.Chain): Response = chain.proceed(chain.request())
}