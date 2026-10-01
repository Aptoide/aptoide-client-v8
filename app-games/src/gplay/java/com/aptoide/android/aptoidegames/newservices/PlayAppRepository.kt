package com.aptoide.android.aptoidegames.newservices

import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.aptoide_network.di.V7Backend
import cm.aptoide.pt.device_api.error.DeviceApiException
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppRepository
import cm.aptoide.pt.feature_apps.data.deviceapi.DeviceApiAppsDataSource
import cm.aptoide.pt.feature_apps.data.isInCatappult
import cm.aptoide.pt.feature_apps.domain.AppSource.Companion.appendIfRequired
import com.aptoide.android.aptoidegames.apkfy.isFreeFirePackage
import com.aptoide.android.aptoidegames.apkfy.isRobloxPackage
import kotlinx.coroutines.CancellationException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * An app's details on the Play build. They come from the new services, except where only v7
 * can serve them:
 *
 * - an app named by its v7 id, which the new services do not know;
 * - the titles that install through Play's details overlay, and the wallet, whose flows stay
 *   on v7;
 * - an app that installs through Aptoide's installer - one flagged for Aptoide billing, or one
 *   the catalog does not hold but v7 flags as a store app - for the download only v7 has.
 *
 * An app outside the catalog that v7 cannot install stays unknown, so that it is not offered
 * from v7 on this build.
 */
@Singleton
internal class PlayAppRepository @Inject constructor(
  @V7Backend private val v7: AppRepository,
  private val newServices: DeviceApiAppsDataSource,
  @StoreName private val storeName: String,
) : AppRepository {

  override suspend fun getApp(packageName: String): App =
    if (packageName.staysOnV7()) {
      v7.getApp(packageName)
    } else {
      resolve(packageName) { v7.getApp(packageName) }
    }

  override suspend fun getAppMeta(source: String): App {
    val packageName = source.packageName()
    return if (packageName == null || packageName.staysOnV7()) {
      v7.getAppMeta(source)
    } else {
      resolve(packageName) { v7.getAppMeta(source.inThisStore()) }
    }
  }

  private suspend fun resolve(packageName: String, fromV7: suspend () -> App): App {
    val app = try {
      newServices.detail(packageName)
    } catch (e: DeviceApiException.NotInVariant) {
      return fromV7OrUnknown(e, fromV7)
    }
    // Flagged apps install through Aptoide's installer, which needs v7's download location
    return if (app.isAppCoins) fromV7() else app
  }

  private suspend fun fromV7OrUnknown(
    notInVariant: DeviceApiException.NotInVariant,
    fromV7: suspend () -> App,
  ): App {
    val app = try {
      fromV7()
    } catch (e: CancellationException) {
      throw e
    } catch (_: Exception) {
      throw notInVariant
    }
    return app.takeIf { it.isInCatappult() == true } ?: throw notInVariant
  }

  // v7 needs the store to find the download, and only this build's store may serve one: a
  // store the source names, as a deep link could, is dropped
  private fun String.inThisStore(): String = split("/")
    .filterNot { it.startsWith(STORE_NAME) }
    .joinToString("/")
    .appendIfRequired(storeName)

  private fun String.staysOnV7(): Boolean =
    isRobloxPackage(this) || isFreeFirePackage(this) || this == WALLET_PACKAGE

  // The package a v7 source names, or null when it names the app another way
  private fun String.packageName(): String? = split("/")
    .firstOrNull { it.startsWith(PACKAGE_NAME) }
    ?.removePrefix(PACKAGE_NAME)
    ?.takeIf { it.isNotBlank() }

  private companion object {
    const val PACKAGE_NAME = "package_name="
    const val STORE_NAME = "store_name="
    const val WALLET_PACKAGE = "com.appcoins.wallet"
  }
}
