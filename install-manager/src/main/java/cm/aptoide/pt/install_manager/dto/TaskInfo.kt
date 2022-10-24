package cm.aptoide.pt.install_manager.dto

import cm.aptoide.pt.install_manager.Task

/**
 * This class represents an ongoing task info.
 */
data class TaskInfo(
  val packageName: String,
  val installPackageInfo: InstallPackageInfo,
  val type: Task.Type,
  val timestamp: Long,
)
