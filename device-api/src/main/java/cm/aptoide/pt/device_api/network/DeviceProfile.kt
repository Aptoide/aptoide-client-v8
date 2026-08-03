package cm.aptoide.pt.device_api.network

import cm.aptoide.pt.environment_info.DeviceInfo
import javax.inject.Inject

/**
 * The requesting device's capabilities, sent as clean query params
 * (`sdk`/`abi`/`tv`/`density`) on search/listings/detail/releases/related, and as
 * a `device` object in the `POST /apps/updates` body. Everything fails OPEN
 * server-side — a missing field never hides results. Replaces v7's base64 `q=`
 * blob entirely (guide §"Device profile", D-050).
 */
data class DeviceProfile(
  val sdk: Int,
  /** Full `Build.SUPPORTED_ABIS`, in order, comma-separated, lowercase. */
  val abi: String,
  val tv: Boolean,
  /** Bucketed density DPI (nearest standard bucket) — see [DeviceProfileProvider]. */
  val density: Int,
)

/** Builds a [DeviceProfile] from the shared [DeviceInfo] (`:environment-info`). */
class DeviceProfileProvider @Inject constructor(
  private val deviceInfo: DeviceInfo,
) {
  fun get(): DeviceProfile = DeviceProfile(
    sdk = deviceInfo.getSdk(),
    abi = deviceInfo.getSupportedABIs(),
    tv = deviceInfo.hasLeanback() == "1",
    // Bucketed (getDensityDpi), NOT the raw densityDpi (getScreenDensity): the
    // device API's detail/releases resolution 404s on non-bucket values like 560
    // (a Pixel 7 Pro's raw dpi). Bucketing 560→640 resolves. Backend should
    // fail-open on density — see .context/contract-friction.md.
    density = deviceInfo.getDensityDpi(),
  )
}
