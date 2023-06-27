package cm.aptoide.pt.network

import android.content.pm.PackageManager
import cm.aptoide.pt.BuildConfig
import cm.aptoide.pt.aptoide_network.data.network.UserAgentInterceptor
import cm.aptoide.pt.environment_info.DeviceInfo
import cm.aptoide.pt.extensions.calculateMD5
import cm.aptoide.pt.extensions.getPackageInfo
import cm.aptoide.pt.network.repository.IdsRepository
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideUserAgentInterceptor @Inject constructor(
  private val packageManager: PackageManager,
  private val idsRepository: IdsRepository,
  private val deviceInfo: DeviceInfo,
) : UserAgentInterceptor {

  private val versionName = BuildConfig.VERSION_NAME
  private val aptoidePackage = BuildConfig.APPLICATION_ID
  private val aptoideVersionCode = BuildConfig.VERSION_CODE

  val cachedMd5: String by lazy {
    packageManager.getPackageInfo(aptoidePackage)
      ?.applicationInfo
      ?.sourceDir
      ?.let(::File)
      ?.calculateMD5()
      ?: "None"
  }

  override fun buildUserAgent(): String =
    "Aptoide/${versionName} " +
      "(Linux; Android ${deviceInfo.getAndroidRelease()}; " +
      "${deviceInfo.getApiLevel()}; " +
      "${deviceInfo.getModel()} " +
      "Build/${deviceInfo.getProductCode()}; " +
      "${deviceInfo.getArchitecture()}; " +
      "$aptoidePackage; " +
      "$aptoideVersionCode; " +
      "${cachedMd5}; " +
      "${deviceInfo.getScreenDimensions()};" +
      "${idsRepository.aptoideClientUuid})"

  override fun intercept(chain: Interceptor.Chain): Response {
    val originalRequest = chain.request()
    val userAgent: String = buildUserAgent()
    val requestBuilder: Request.Builder = originalRequest.newBuilder()
    requestBuilder.header("User-Agent", userAgent)
    return chain.proceed(requestBuilder.build())
  }
}
