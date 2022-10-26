package cm.aptoide.pt.install_manager.dto

/**
 * This class represents a known app info.
 * @param D the type of an app details.
 * @property packageName - an app package name.
 * @property installedVersion - an app version installed. Null if app is not installed.
 * @property details - additional app details.
 */
data class AppInfo<D>(
  val packageName: String,
  val installedVersion: Version? = null,
  val details: D? = null
)