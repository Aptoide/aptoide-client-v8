package cm.aptoide.pt.device_api.error

/**
 * Typed device-API failures mapped from RFC 9457 problem+json. The three that
 * carry product meaning on app routes (guide §3): 410 removed, 451 country,
 * 404 not-in-variant. Everything else surfaces as [Generic].
 */
sealed class DeviceApiException(
  val statusCode: Int,
  message: String?,
  cause: Throwable? = null,
) : Exception(message, cause) {

  /** 410 — removed by serving policy. [reason] is public and MUST be shown. */
  class Removed(val reason: String?) : DeviceApiException(410, reason)

  /** 451 — unavailable in the requesting country. */
  class CountryUnavailable(reason: String?) : DeviceApiException(451, reason)

  /** 404 — not present in this variant/catalog (or unknown package). */
  class NotInVariant(reason: String?) : DeviceApiException(404, reason)

  /** Any other non-2xx (422/5xx/transport). */
  class Generic(statusCode: Int, message: String?, cause: Throwable? = null) :
    DeviceApiException(statusCode, message, cause)
}
