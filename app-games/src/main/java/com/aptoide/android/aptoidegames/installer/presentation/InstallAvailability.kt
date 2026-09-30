package com.aptoide.android.aptoidegames.installer.presentation

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.domain.AppOrigin
import com.aptoide.android.aptoidegames.apkfy.isFreeFire
import com.aptoide.android.aptoidegames.apkfy.isRoblox
import com.aptoide.android.aptoidegames.installer.CatalogStatus
import com.aptoide.android.aptoidegames.installer.excludedFromPlayCatalog

/** Whether an install can be offered at all. */
enum class InstallAvailability {
  AVAILABLE,

  /** The catalog is being looked up; the answer decides. */
  CHECKING,

  /** Play cannot install the app and nothing else may, so no install is offered. */
  NOT_OFFERED,
}

/**
 * An app of the new services that Play's catalog does not hold is not offered: it installs
 * through Play only, and Play cannot install it. Everything else is available - v7 apps
 * install through Aptoide when Play cannot, apps that install through Aptoide never needed
 * a token, and the overlay titles install through Play's overlay.
 *
 * Only decided on the app view, which looks the catalog up: cards do not, and never withhold
 * an install. A [status] of null means there is no catalog to ask on this build. A lookup
 * that failed keeps the app available, so a flaky connection never hides an install: the tap
 * looks the catalog up again.
 */
fun installAvailability(
  app: App,
  status: CatalogStatus?,
  onAppView: Boolean,
): InstallAvailability {
  val decidedByCatalog = onAppView &&
    status != null &&
    app.origin == AppOrigin.DEVICE_API &&
    !app.excludedFromPlayCatalog() &&
    !app.isRoblox() &&
    !app.isFreeFire()
  if (!decidedByCatalog) return InstallAvailability.AVAILABLE
  return when (status) {
    CatalogStatus.UNKNOWN -> InstallAvailability.CHECKING
    CatalogStatus.NOT_IN_CATALOG -> InstallAvailability.NOT_OFFERED
    CatalogStatus.IN_CATALOG, CatalogStatus.FAILED -> InstallAvailability.AVAILABLE
  }
}
