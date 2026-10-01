package cm.aptoide.pt.device_api.error

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import retrofit2.HttpException

/**
 * Translates a Retrofit [HttpException] into a typed [DeviceApiException] by parsing the
 * RFC 9457 problem+json body. The status decides the type, so a body that cannot be read
 * costs the reason but never the type.
 */
object ProblemMapper {

  // An identifier, not an address: the service sends this same value from every environment
  private const val AGE_RESTRICTED_TYPE = "https://api.aptoide.com/problems/age-restricted"

  private val gson = Gson()

  fun map(e: HttpException): DeviceApiException {
    val problem = parseProblem(e)
    // A blank detail cannot be shown, so it counts as missing
    val reason = problem?.detail?.takeIf { it.isNotBlank() }
      ?: problem?.title?.takeIf { it.isNotBlank() }
    return when (e.code()) {
      410 -> DeviceApiException.Removed(reason, e)
      451 -> DeviceApiException.CountryUnavailable(reason, e)
      404 -> DeviceApiException.NotInVariant(reason, e)
      502 -> DeviceApiException.Upstream(reason, e)
      403 -> if (problem?.type == AGE_RESTRICTED_TYPE) {
        DeviceApiException.AgeRestricted(problem.ageRating, reason, e)
      } else {
        DeviceApiException.Generic(e.code(), reason, e)
      }

      else -> DeviceApiException.Generic(e.code(), reason, e)
    }
  }

  private fun parseProblem(e: HttpException): Problem? = runCatching {
    e.response()?.errorBody()?.string()
      ?.takeIf { it.isNotBlank() }
      ?.let { gson.fromJson(it, Problem::class.java) }
  }.getOrNull()
}

/**
 * Runs a device API call, turning service failures into a typed [DeviceApiException].
 *
 * Only failures that say something about the service are translated: an error status, or a
 * body that does not match the contract. Connectivity errors and cancellation are rethrown
 * as they are - callers tell "no connection" apart by [java.io.IOException], and a swallowed
 * cancellation would keep cancelled work alive.
 *
 * [MalformedJsonException] is the one [java.io.IOException] that is translated: it is what the
 * converter throws for a body that is not json, which is the service's fault. A body cut
 * short surfaces as an EOFException and stays a connectivity error, since a dropped
 * connection looks exactly the same.
 */
suspend fun <T> deviceApiCall(block: suspend () -> T): T =
  try {
    block()
  } catch (e: HttpException) {
    throw ProblemMapper.map(e)
  } catch (e: JsonParseException) {
    throw DeviceApiException.Generic(DeviceApiException.NO_STATUS, e.message, e)
  } catch (e: MalformedJsonException) {
    throw DeviceApiException.Generic(DeviceApiException.NO_STATUS, e.message, e)
  }
