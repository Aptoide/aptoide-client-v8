package cm.aptoide.pt.aptoide_network.data.network

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response
import javax.inject.Inject

class LanguageInterceptor @Inject constructor(
  @ApplicationContext context: Context
) : Interceptor {

  private val resources = context.resources

  override fun intercept(chain: Chain): Response {
    val originalRequest = chain.request()
    val newUrl = originalRequest.url.newBuilder()
      .addQueryParameter(
        "lang", resources.configuration.locale.language
          + "_"
          + resources.configuration.locale.country
      ).build()
    val newRequest = originalRequest.newBuilder().url(newUrl).build()
    return chain.proceed(newRequest)
  }
}
