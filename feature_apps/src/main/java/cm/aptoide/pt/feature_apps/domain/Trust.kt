package cm.aptoide.pt.feature_apps.domain

/**
 * Transparency / trust signals surfaced on app detail — the product's
 * "transparency inversion" (device.openapi.json `TrustResponse`). Populated only
 * on the device-API (aptoideGamesDev) path; null on the v7 path.
 */
data class Trust(
  /** trusted | unknown | warning | critical */
  val scanVerdict: String?,
  val signerSha256: String?,
  /** consistent | different_signer | unknown */
  val signerConsistency: String?,
  /** developer_upload | mirrored | unknown */
  val provenance: String?,
)
