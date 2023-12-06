package cm.aptoide.pt.install_manager.environment

/**
 * This interface represents network check logic.
 *
 * @property state - current network state.
 * errors and should never finish.
 */
interface NetworkConnection {

  /**
   * Sets a listener for network changes.
   *
   * @param onChange - a callback to signal about network changes.
   */
  fun setOnChangeListener(onChange: (State) -> Unit)

  enum class State {
    /* No network available */
    GONE,

    /* Network is metered */
    METERED,

    /* Network is not metered */
    UNMETERED,
  }
}
