package cm.aptoide.pt.usage_stats

import kotlinx.coroutines.flow.Flow

interface PackageUsageManager {

  val foregroundPackage: Flow<String?>

  fun getForegroundPackage(): String?

  fun getForegroundPackageState(startTimeMs: Long? = null): PackageUsageState
}

sealed class PackageUsageState {

  /**
   * [packageName] has the most recently resumed activity. [sharesScreen] is true when another app
   * is resumed at the same time (split-screen with multi-resume, Android 10+); see
   * [ForegroundActivityResolver] for what can and cannot be detected.
   */
  data class ForegroundPackage(
    val packageName: String,
    val sharesScreen: Boolean = false,
  ) : PackageUsageState()

  data object NoForegroundPackage : PackageUsageState()

  data object Error : PackageUsageState()
}
