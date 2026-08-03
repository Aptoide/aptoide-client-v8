package cm.aptoide.pt.device_api.error

import com.google.gson.Gson
import retrofit2.HttpException

/**
 * Translates a Retrofit [HttpException] into a typed [DeviceApiException] by
 * parsing the RFC 9457 problem+json body. Repositories wrap their device-API
 * calls in [deviceApiCall] so callers only ever see typed failures.
 */
object ProblemMapper {

  private val gson = Gson()

  fun map(e: HttpException): DeviceApiException {
    val reason = parseReason(e)
    return when (e.code()) {
      410 -> DeviceApiException.Removed(reason)
      451 -> DeviceApiException.CountryUnavailable(reason)
      404 -> DeviceApiException.NotInVariant(reason)
      else -> DeviceApiException.Generic(e.code(), reason ?: e.message(), e)
    }
  }

  private fun parseReason(e: HttpException): String? = runCatching {
    e.response()?.errorBody()?.string()?.takeIf { it.isNotBlank() }
      ?.let { gson.fromJson(it, Problem::class.java) }
      ?.let { it.detail ?: it.title }
  }.getOrNull()
}

/** Runs a device-API call, mapping [HttpException] to a typed [DeviceApiException]. */
suspend fun <T> deviceApiCall(block: suspend () -> T): T =
  try {
    block()
  } catch (e: HttpException) {
    throw ProblemMapper.map(e)
  }
