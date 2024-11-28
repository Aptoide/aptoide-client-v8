package cm.aptoide.pt.network

import android.content.Context
import cm.aptoide.pt.aptoide_network.data.network.QueryLangInterceptor
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor.Chain
import okhttp3.Response
import javax.inject.Inject

class AptoideQueryLangInterceptor @Inject constructor(
  @ApplicationContext private val context: Context
) : QueryLangInterceptor {

  override fun buildLang(): String {
    val locale = context.resources.configuration.locales[0]
    return "${locale.language}_${locale.country}"
  }

  override fun intercept(chain: Chain): Response {
    val originalRequest = chain.request()
    val newUrl = originalRequest.url.newBuilder()
    newUrl.addQueryParameter("lang", buildLang())
    val newRequest = originalRequest.newBuilder().url(newUrl.build()).build()
    return chain.proceed(newRequest)
  }
}
