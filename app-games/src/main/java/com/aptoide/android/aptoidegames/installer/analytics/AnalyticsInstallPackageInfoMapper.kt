package com.aptoide.android.aptoidegames.installer.analytics

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.hasObb
import cm.aptoide.pt.feature_apps.data.isAab
import cm.aptoide.pt.feature_apps.data.isInCatappult
import cm.aptoide.pt.install_info_mapper.domain.InstallPackageInfoMapper
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.InstallationFile
import cm.aptoide.pt.installer.TemporaryPayload
import cm.aptoide.pt.installer.TemporaryPayload.Companion.fromString
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsPayload
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsUIContext
import com.google.gson.Gson

class AnalyticsInstallPackageInfoMapper(private val mapper: InstallPackageInfoMapper) :
  InstallPackageInfoMapper {

  override suspend fun map(app: App): InstallPackageInfo {
    // Immediately snapshot the current analytics context to avoid it's override while mapping
    val context = currentAnalyticsUIContext
    // Map overriding the payload
    return mapper.map(app).run {
      val temporaryPayload: TemporaryPayload? = payload?.fromString()
      copy(
        payload = AnalyticsPayload(
          isApkfy = false, // TODO: Add when ready
          isAab = temporaryPayload?.isAab ?: app.isAab(),
          aabTypes = getSplitTypesAnalyticsString(
            installationFiles = installationFiles
          ),
          isAppCoins = app.isAppCoins,
          isInCatappult = temporaryPayload?.isInCatappult ?: app.isInCatappult(),
          hasObb = temporaryPayload?.hasObb ?: app.hasObb(),
          versionCode = app.versionCode,
          context = context.currentScreen,
          previousContext = context.previousScreen,
          store = app.store.storeName,
          bundleMeta = context.bundleMeta,
          searchMeta = context.searchMeta,
          itemPosition = context.itemPosition,
          trustedBadge = temporaryPayload?.trustedBadge,
        ).let<AnalyticsPayload, String?>(Gson()::toJson)
      )
    }
  }

  companion object {
    var currentAnalyticsUIContext: AnalyticsUIContext = AnalyticsUIContext.Empty
  }
}

private fun getSplitTypesAnalyticsString(installationFiles: Set<InstallationFile>): String {
  var baseCount = 0
  var hasPAD = false
  var hasPFD = false

  installationFiles.forEach {
    if (it.type == InstallationFile.Type.BASE) {
      baseCount++
    } else if (it.type == InstallationFile.Type.PFD_INSTALL_TIME) {
      hasPFD = true
    } else if (it.type == InstallationFile.Type.PAD_INSTALL_TIME) {
      hasPAD = true
    }
  }

  return if (baseCount < 2) {
    "false"
  } else if (hasPFD && hasPAD) {
    "PAD+PFD"
  } else if (hasPFD) {
    "PFD"
  } else if (hasPAD) {
    "PAD"
  } else {
    "base"
  }
}

fun String?.toAnalyticsPayload(): AnalyticsPayload? = this?.let {
  runCatching {
    Gson().fromJson(it, AnalyticsPayload::class.java)
  }.getOrNull()
}
