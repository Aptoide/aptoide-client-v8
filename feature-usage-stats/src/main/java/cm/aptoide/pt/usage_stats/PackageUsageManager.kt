package cm.aptoide.pt.usage_stats

import kotlinx.coroutines.flow.Flow

interface PackageUsageManager {

  val foregroundPackage: Flow<String?>

  fun getForegroundPackage(): String?
}
