package cm.aptoide.pt.install_manager.environment

/**
 * This interface represents free space check logic.
 */
interface FreeSpaceChecker {

  /**
   * Check if there will be enough free space for an app download and install in future.
   *
   * @param appSize - size of an app that is going to be installed
   * @param scheduledSize - total memory size that is scheduled to be occupied before the files. If null, don't take it into account.
   * download will start.
   * @returns how much free space will be missing by the time of start. 0 or negative if it will be enough.
   */
  fun missingSpace(appSize: Long, scheduledSize: Long? = null): Long
}
