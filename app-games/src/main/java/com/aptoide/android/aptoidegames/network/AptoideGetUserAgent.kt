package com.aptoide.android.aptoidegames.network

import android.content.pm.PackageManager
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.network.repository.IdsRepository
import cm.aptoide.pt.aptoide_network.data.network.GetUserAgent
import cm.aptoide.pt.environment_info.DeviceInfo
import cm.aptoide.pt.extensions.calculateMD5
import cm.aptoide.pt.extensions.getPackageInfo
import com.appcoins.payments.network.RestClientInjectParams
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideGetUserAgent @Inject constructor(
  private val packageManager: PackageManager,
  private val idsRepository: IdsRepository,
  private val deviceInfo: DeviceInfo,
) : GetUserAgent, RestClientInjectParams {

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

  override operator fun invoke(): String =
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

  override fun getUserAgent(): String = invoke()

  override val channel: String = "aptoide-games"
}
