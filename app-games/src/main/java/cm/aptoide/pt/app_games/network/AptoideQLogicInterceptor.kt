package cm.aptoide.pt.app_games.network

import android.util.Base64
import cm.aptoide.pt.aptoide_network.data.network.QLogicInterceptor
import cm.aptoide.pt.environment_info.DeviceInfo
import okhttp3.Interceptor.Chain
import okhttp3.Response
import javax.inject.Inject

class AptoideQLogicInterceptor @Inject constructor(
  private val deviceInfo: DeviceInfo
) : QLogicInterceptor {

  private val cachedFilters: String by lazy { computeFilters() }

  override fun buildQValue(): String = cachedFilters

  override fun intercept(chain: Chain): Response {
    val originalRequest = chain.request()
    val newUrl = originalRequest.url.newBuilder()
    buildQValue().let {
      newUrl.addQueryParameter("q", buildQValue())
    }
    val newRequest = originalRequest.newBuilder().url(newUrl.build()).build()
    return chain.proceed(newRequest)
  }

  private fun computeFilters(): String {
    val filters = "maxSdk=${deviceInfo.getApiLevel()}" +
      "&maxScreen=${deviceInfo.getScreenSize()}" +
      "&maxGles=${deviceInfo.getGlEsVersion()}" +
      "&myCPU=${deviceInfo.getSupportedABIs()}" +
      "&leanback=${deviceInfo.hasLeanback()}" +
      "&myDensity=${deviceInfo.getDensityDpi()}"

    return Base64.encodeToString(filters.toByteArray(), 0)
      .replace("=", "")
      .replace("/", "*")
      .replace("+", "_")
      .replace("\n", "")
  }
}
