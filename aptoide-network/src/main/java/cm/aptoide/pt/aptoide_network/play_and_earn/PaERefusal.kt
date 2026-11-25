package cm.aptoide.pt.aptoide_network.play_and_earn

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import retrofit2.HttpException
import java.time.Instant
import java.time.OffsetDateTime

/**
 * Why the Play & Earn backend refused a request (AND-950): its `detail.code`. The app maps each
 * code to its own translated text; the backend's `message` is for logs and is never shown.
 */
enum class PaERefusalCode {
  ACCOUNT_RESTRICTED,
  REGION_NOT_SUPPORTED,
  DEVICE_NOT_SUPPORTED,
  UPDATE_REQUIRED,
  REDEEM_LIMIT,
  INSTALL_LIMIT,
  TOO_MANY_ATTEMPTS,
  REDEEM_IN_PROGRESS,
  REDEEM_UNDER_REVIEW,
  REDEEM_REQUEST_MISMATCH,
  REDEEM_OUTCOME_UNKNOWN,
}

/** @param retryAt when the request may be tried again, for the limits that say so. */
data class PaERefusal(
  val code: PaERefusalCode,
  val retryAt: Instant? = null,
)

/** A refusal already read from its HTTP error, for repositories that return typed failures. */
class PaERefusalException(
  val refusal: PaERefusal,
  cause: Throwable? = null,
) : Exception("Play & Earn refused the request: ${refusal.code}", cause)

/**
 * The one parser of Play & Earn refusals. A refusal is the only error of that API whose `detail`
 * is an object:
 * `{"detail": {"code": "REDEEM_LIMIT", "message": "...", "retry_at": "2026-11-20T14:05:00Z"}}`.
 * Every other error (a string or validation-list `detail`, an unknown or missing code, a body
 * that is not JSON, a network failure) is not a refusal and keeps the caller's generic error.
 * Never throws, and reading does not consume the error body.
 */
object PaERefusals {

  fun from(throwable: Throwable?): PaERefusal? {
    var current = throwable
    var depth = 0
    while (current != null && depth < MAX_CAUSE_DEPTH) {
      when (current) {
        is PaERefusalException -> return current.refusal
        is HttpException -> return fromErrorBody(current.peekErrorBody())
      }
      current = current.cause?.takeIf { it !== current }
      depth++
    }
    return null
  }

  fun fromErrorBody(body: String?): PaERefusal? {
    if (body.isNullOrBlank()) return null
    return try {
      val root = JsonParser.parseString(body).objectOrNull() ?: return null
      val detail = root.get("detail")?.objectOrNull() ?: return null
      val code = detail.stringOrNull("code") ?: return null
      val refusalCode = PaERefusalCode.entries.firstOrNull { it.name == code } ?: return null
      PaERefusal(
        code = refusalCode,
        retryAt = detail.stringOrNull("retry_at")?.toInstantOrNull(),
      )
    } catch (e: Exception) {
      null
    }
  }

  private const val MAX_CAUSE_DEPTH = 8
}

/** Shorthand for [PaERefusals.from]. */
fun Throwable.toPaERefusal(): PaERefusal? = PaERefusals.from(this)

private fun HttpException.peekErrorBody(): String? = try {
  // Peeked, not read: whoever reads the body next still gets all of it.
  response()?.errorBody()?.source()?.peek()?.readUtf8()
} catch (e: Exception) {
  null
}

private fun JsonElement.objectOrNull(): JsonObject? = if (isJsonObject) asJsonObject else null

private fun JsonObject.stringOrNull(name: String): String? = get(name)
  ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isString }
  ?.asString

private fun String.toInstantOrNull(): Instant? =
  runCatching { Instant.parse(this) }.getOrNull()
    ?: runCatching { OffsetDateTime.parse(this).toInstant() }.getOrNull()
