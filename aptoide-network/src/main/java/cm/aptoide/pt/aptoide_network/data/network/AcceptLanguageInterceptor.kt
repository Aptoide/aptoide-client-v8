package cm.aptoide.pt.aptoide_network.data.network

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response
import javax.inject.Inject

class AcceptLanguageInterceptor @Inject constructor(
  @ApplicationContext private val context: Context
) : Interceptor {

  override fun intercept(chain: Chain): Response {
    val resources = context.resources
    val originalRequest = chain.request()
    val newRequest = originalRequest.newBuilder()
      .addHeader(
        "Accept-Language", resources.configuration.locale.language
          + "-"
          + resources.configuration.locale.country
      ).build()
    return chain.proceed(newRequest)
  }
}
