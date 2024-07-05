package cm.aptoide.pt.install_manager.environment

/**
 * This interface represents device storage.
 */
interface DeviceStorage {

  /**
   * Gives current value of the available free space on the device.
   *
   * @returns how much free space is available currently.
   */
  val availableFreeSpace: Long
}
