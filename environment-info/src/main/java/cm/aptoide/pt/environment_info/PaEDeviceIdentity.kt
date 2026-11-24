package cm.aptoide.pt.environment_info

/**
 * Identifies the phone to the Play & Earn backend, so its device rules can tell which phone a
 * request comes from. The app only sends this evidence; the backend decides.
 */
interface PaEDeviceIdentity {

  /**
   * SHA-256 (lower-case hex) of `Settings.Secure.ANDROID_ID`, or null when the Android ID can't be
   * read. The raw Android ID never leaves the device.
   */
  suspend fun getDeviceKey(): String?

  /**
   * A random UUID created the first time it is asked for and kept from then on. It tells a
   * reinstall (new id, same device key) and a copied Android ID (same key, other id) apart.
   */
  suspend fun getInstallId(): String
}
