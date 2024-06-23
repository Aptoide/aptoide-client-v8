package com.aptoide.android.aptoidegames.installer.analytics

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.install_info_mapper.domain.InstallPackageInfoMapper
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsPayload
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsUIContext
import com.google.gson.Gson

class AnalyticsInstallPackageInfoMapper(private val mapper: InstallPackageInfoMapper) :
  InstallPackageInfoMapper {

  override suspend fun map(app: App): InstallPackageInfo {
    // Immediately snapshot the current analytics context to avoid it's override while mapping
    val context = currentAnalyticsUIContext
    // Map overriding the payload
    return mapper.map(app).copy(
      payload = AnalyticsPayload(
        isApkfy = false, // TODO: Add when ready
        isAab = false, // TODO: Add when ready
        aabInstallTime = "false", // TODO: Add when ready
        isAppCoins = app.isAppCoins,
        isInCatappult = false, // TODO: Add when ready
        isGame = true, // TODO: Add when ready
        isMigration = false, // TODO: Add when ready
        hasObb = app.obb != null, // TODO: Add when ready
        versionCode = app.versionCode,
        context = context.currentScreen,
        previousContext = context.previousScreen,
        store = app.store.storeName,
        bundleMeta = context.bundleMeta,
        trustedBadge = app.malware,
      ).let<AnalyticsPayload, String?>(Gson()::toJson)
    )
  }

  companion object {
    var currentAnalyticsUIContext: AnalyticsUIContext = AnalyticsUIContext.Empty
  }
}

fun String?.toAnalyticsPayload(): AnalyticsPayload? = this?.let {
  runCatching {
    Gson().fromJson(it, AnalyticsPayload::class.java)
  }.getOrNull()
}