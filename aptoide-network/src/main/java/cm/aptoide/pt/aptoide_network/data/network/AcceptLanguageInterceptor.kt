package cm.aptoide.pt.aptoide_network.data.network

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AcceptLanguageInterceptor @Inject constructor(@ApplicationContext private val context: Context) : Interceptor {

  override fun intercept(chain: Chain): Response {
    val locale = context.resources.configuration.locales[0]
    val language = "${locale.language}-${locale.country}, ${locale.language};q=0.9, *;q=0.7"
    val originalRequest = chain.request()
    val newRequest = originalRequest.newBuilder()
      .addHeader("Accept-Language", language)
      .build()
    return chain.proceed(newRequest)
  }
}
