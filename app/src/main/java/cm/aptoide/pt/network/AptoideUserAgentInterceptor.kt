package cm.aptoide.pt.network

import android.util.DisplayMetrics
import cm.aptoide.pt.aptoide_network.data.network.UserAgentInterceptor
import cm.aptoide.pt.network.model.AptoideMd5Manager
import cm.aptoide.pt.network.model.IdsRepository
import cm.aptoide.pt.settings.data.DeviceInfoRepository
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideUserAgentInterceptor @Inject constructor(
  private val idsRepository: IdsRepository?,
  private val architecture: String?,
  private val displayMetrics: DisplayMetrics,
  private val versionName: String,
  private val aptoidePackage: String,
  private val aptoideMd5Manager: AptoideMd5Manager?,
  private val aptoideVersionCode: Int,
  private val deviceInfoRepository: DeviceInfoRepository,
) : UserAgentInterceptor {

  override fun buildUserAgent(): String {
    val metricsWidth = displayMetrics.widthPixels
    val metricsHeight = displayMetrics.heightPixels

    return "Aptoide/${versionName} " +
      "(Linux; Android ${deviceInfoRepository.getAndroidVersion()}; " +
      "${deviceInfoRepository.getApiLevel()}; " +
      "${deviceInfoRepository.getBuildModel()} " +
      "Build/${deviceInfoRepository.getProductCode()}; " +
      "$architecture; " +
      "$aptoidePackage; " +
      "$aptoideVersionCode; " +
      "${aptoideMd5Manager/*.getAptoideMd5()*/}; " +
      "${metricsWidth}x$metricsHeight;)"
  }

  override fun intercept(chain: Interceptor.Chain): Response {
    val originalRequest = chain.request()
    val userAgent: String = buildUserAgent()
    val requestBuilder: Request.Builder = originalRequest.newBuilder()
    requestBuilder.header("User-Agent", userAgent)
    return chain.proceed(requestBuilder.build())
  }
}
