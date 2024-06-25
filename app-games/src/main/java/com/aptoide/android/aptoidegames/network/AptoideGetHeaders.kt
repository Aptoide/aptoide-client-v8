package com.aptoide.android.aptoidegames.network

import android.content.Context
import android.content.pm.PackageManager
import cm.aptoide.pt.aptoide_network.data.network.GetAcceptLanguage
import cm.aptoide.pt.aptoide_network.data.network.GetUserAgent
import cm.aptoide.pt.environment_info.DeviceInfo
import cm.aptoide.pt.extensions.calculateMD5
import cm.aptoide.pt.extensions.getPackageInfo
import com.appcoins.payments.network.RestClientInjectParams
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.network.repository.IdsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideGetHeaders @Inject constructor(
  @ApplicationContext private val context: Context,
  private val packageManager: PackageManager,
  private val idsRepository: IdsRepository,
  private val deviceInfo: DeviceInfo,
) : GetUserAgent, GetAcceptLanguage, RestClientInjectParams {

  private val versionName = BuildConfig.VERSION_NAME
  private val aptoidePackage = BuildConfig.APPLICATION_ID
  private val aptoideVersionCode = BuildConfig.VERSION_CODE

  private val cachedMd5: String by lazy {
    packageManager.getPackageInfo(aptoidePackage)
      ?.applicationInfo
      ?.sourceDir
      ?.let(::File)
      ?.calculateMD5()
      ?: "None"
  }

  override fun getUserAgent(): String =
    "AppGames/${versionName} " +
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

  override fun getAcceptLanguage(): String {
    val locale = context.resources.configuration.locales[0]
    return "${locale.language}-${locale.country}, ${locale.language};q=0.9, *;q=0.7"
  }

  override val channel: String = "ANDROID"
}
