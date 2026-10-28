package cm.aptoide.pt.feature_apkfy.domain

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.domain.AppOrigin

/** How apkfy's title resolved, and so what the user sees and what gets reported. */
sealed interface ApkfyResolution {

  /**
   * The title, from the catalog its [App.origin] names. With an [unavailableReason] it is
   * shown but cannot be installed.
   */
  data class Resolved(
    val app: App,
    val unavailableReason: UnavailableReason? = null,
  ) : ApkfyResolution

  /** No catalog holds the title. */
  data class NotFound(val packageName: String?) : ApkfyResolution

  /** The lookup in [source] failed with [error], so whether the title exists is unknown. */
  data class Failed(val source: AppOrigin, val error: Throwable) : ApkfyResolution
}

/** Why a resolved title cannot be installed. */
enum class UnavailableReason(val analyticsValue: String) {
  /** The catalog lists the title but serves no files for it yet. */
  NOT_INSTALLABLE_YET("not_installable_yet"),

  /** The release needs a newer Android than the device runs. */
  MIN_SDK("min_sdk"),

  /** The release has splits for this device but no base apk to install them on. */
  NO_COMPATIBLE_SPLIT("no_compatible_split"),
}
