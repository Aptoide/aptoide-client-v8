package cm.aptoide.pt.device_api.network

import android.os.Build
import cm.aptoide.pt.environment_info.DeviceInfo
import javax.inject.Inject

/**
 * The requesting device's capabilities, sent as the `sdk`/`abi`/`tv`/`density` query
 * parameters on catalog reads and as the `device` object of the updates request. The service
 * fails open on every one of them - a missing field never hides results.
 */
data class DeviceProfile(
  val sdk: Int,
  /** The supported abis in the device's order of preference. */
  val abis: List<String>,
  val tv: Boolean,
  /** Density dpi rounded to a standard bucket - see [DeviceProfileProvider]. */
  val density: Int,
) {
  /**
   * [abis] as the single comma separated value the query parameter takes, or null when there
   * are none, so that the parameter is left out instead of being sent empty.
   */
  val abi: String? get() = abis.takeIf { it.isNotEmpty() }?.joinToString(separator = ",")
}

fun deviceProfileOf(
  sdk: Int,
  abis: List<String>,
  isTv: Boolean,
  bucketedDensity: Int,
): DeviceProfile = DeviceProfile(
  sdk = sdk,
  abis = abis.filter { it.isNotBlank() },
  tv = isTv,
  density = bucketedDensity,
)

/** Builds the [DeviceProfile] of this device from the shared [DeviceInfo]. */
class DeviceProfileProvider @Inject constructor(
  private val deviceInfo: DeviceInfo,
) {
  fun get(): DeviceProfile = deviceProfileOf(
    sdk = deviceInfo.getSdk(),
    abis = Build.SUPPORTED_ABIS.orEmpty().toList(),
    isTv = deviceInfo.hasLeanback() == "1",
    // The bucketed value, not the raw densityDpi: the service answers 404 on detail for a
    // density outside the standard buckets, such as the 560 a Pixel 7 Pro reports
    bucketedDensity = deviceInfo.getDensityDpi(),
  )
}
