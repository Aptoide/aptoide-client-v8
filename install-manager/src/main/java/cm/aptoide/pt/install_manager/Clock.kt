package cm.aptoide.pt.install_manager

/**
 * This represents an injectable clock
 */
fun interface Clock {

  /**
   * Get current timestamp
   */
  fun getCurrentTimeStamp(): Long
}