package cm.aptoide.pt.device_api.error

/**
 * Typed device API failures, mapped from RFC 9457 problem+json. Each status that carries
 * product meaning has its own type; everything else surfaces as [Generic].
 *
 * Connectivity failures are not represented here - they reach callers as the original
 * [java.io.IOException], see [deviceApiCall].
 */
sealed class DeviceApiException(
  val statusCode: Int,
  message: String?,
  cause: Throwable? = null,
) : Exception(message, cause) {

  /** 410 - removed by serving policy. [reason] is public and meant to be shown. */
  class Removed(val reason: String?, cause: Throwable? = null) :
    DeviceApiException(410, reason, cause)

  /** 451 - unavailable in the requesting country. */
  class CountryUnavailable(reason: String?, cause: Throwable? = null) :
    DeviceApiException(451, reason, cause)

  /** 404 - not present in the requested variant, or an unknown package. */
  class NotInVariant(reason: String?, cause: Throwable? = null) :
    DeviceApiException(404, reason, cause)

  /** 403 with the age-restricted problem type. [ageRating] is the minimum age required. */
  class AgeRestricted(val ageRating: Int?, reason: String?, cause: Throwable? = null) :
    DeviceApiException(403, reason, cause)

  /** 502 - the service is up but the source it reads from is not. */
  class Upstream(reason: String?, cause: Throwable? = null) :
    DeviceApiException(502, reason, cause)

  /** Any other failure, including a response that does not match the contract. */
  class Generic(statusCode: Int, message: String?, cause: Throwable? = null) :
    DeviceApiException(statusCode, message, cause)

  companion object {
    /** Status of a failure that did not come from an HTTP response. */
    const val NO_STATUS = 0
  }
}
