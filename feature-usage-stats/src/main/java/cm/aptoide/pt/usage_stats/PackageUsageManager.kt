package cm.aptoide.pt.usage_stats

import kotlinx.coroutines.flow.Flow

interface PackageUsageManager {

  val foregroundPackage: Flow<String?>

  fun getForegroundPackage(): String?

  fun getForegroundPackageState(startTimeMs: Long? = null): PackageUsageState
}

sealed class PackageUsageState {

  data class ForegroundPackage(val packageName: String) : PackageUsageState()

  data object NoForegroundPackage : PackageUsageState()

  data object Error : PackageUsageState()
}
