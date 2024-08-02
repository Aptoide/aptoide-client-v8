package cm.aptoide.pt.install_manager.dto

data class Constraints(
  val checkForFreeSpace: Boolean,
  val networkType: NetworkType,
) {

  /**
   * An enumeration of various network types that can be used as [Constraints] for installation.
   */
  enum class NetworkType {
    /**
     * A network is not required for this task.
     */
    NOT_REQUIRED,

    /**
     * Any working network connection is required for this task.
     */
    ANY,

    /**
     * An unmetered network connection is required for this task.
     */
    UNMETERED,
  }
}
