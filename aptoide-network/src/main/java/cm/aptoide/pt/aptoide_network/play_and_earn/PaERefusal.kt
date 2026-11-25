package cm.aptoide.pt.aptoide_network.play_and_earn

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import retrofit2.HttpException
import timber.log.Timber
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * Why the Play & Earn backend refused a request (AND-950): its `detail.code`. The app maps each
 * code to its own translated text; the backend's `message` is for logs and is never shown.
 *
 * @param httpStatus the only status the backend answers this code with: the same body on any
 * other status (e.g. a proxy's 502 page) is not a refusal.
 * @param blocksPlay the code is about playing at all, so it may end a Play & Earn session. The
 * others (limits, redeem states) leave a session as any other failure would.
 */
enum class PaERefusalCode(val httpStatus: Int, val blocksPlay: Boolean) {
  ACCOUNT_RESTRICTED(403, blocksPlay = true),
  REGION_NOT_SUPPORTED(403, blocksPlay = true),
  DEVICE_NOT_SUPPORTED(403, blocksPlay = true),
  UPDATE_REQUIRED(426, blocksPlay = true),
  REDEEM_LIMIT(429, blocksPlay = false),
  INSTALL_LIMIT(429, blocksPlay = false),
  TOO_MANY_ATTEMPTS(429, blocksPlay = false),
  REDEEM_IN_PROGRESS(409, blocksPlay = false),
  REDEEM_UNDER_REVIEW(409, blocksPlay = false),
  REDEEM_REQUEST_MISMATCH(409, blocksPlay = false),
  REDEEM_OUTCOME_UNKNOWN(504, blocksPlay = false),
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
 * `{"detail": {"code": "REDEEM_LIMIT", "message": "...", "retry_at": "2026-11-20T14:05:00Z"}}`,
 * on the HTTP status [PaERefusalCode.httpStatus] documents for its code. Every other error (a
 * string or validation-list `detail`, an unknown or missing code, a code on another status, a
 * body that is not JSON, a network failure) is not a refusal and keeps the caller's generic
 * error. Never throws, reads at most [MAX_BODY_BYTES] of the body and does not consume it.
 */
object PaERefusals {

  fun from(throwable: Throwable?): PaERefusal? {
    var current = throwable
    var depth = 0
    while (current != null && depth < MAX_CAUSE_DEPTH) {
      when (current) {
        is PaERefusalException -> return current.refusal
        is HttpException -> return fromErrorBody(current.code(), current.peekErrorBody())
      }
      current = current.cause?.takeIf { it !== current }
      depth++
    }
    return null
  }

  fun fromErrorBody(httpStatus: Int, body: String?): PaERefusal? {
    if (body.isNullOrBlank()) return null
    return try {
      val root = JsonParser.parseString(body).objectOrNull() ?: return null
      val detail = root.get("detail")?.objectOrNull() ?: return null
      val code = detail.stringOrNull("code") ?: return null
      val refusalCode = PaERefusalCode.entries
        .firstOrNull { it.name == code && it.httpStatus == httpStatus }
        ?: return null
      PaERefusal(
        code = refusalCode,
        retryAt = detail.stringOrNull("retry_at")?.toInstantOrNull(),
      )
    } catch (e: Exception) {
      null
    }
  }

  /** An error body this long is not a refusal; a proxy's error page must not fill memory. */
  const val MAX_BODY_BYTES = 64L * 1024L

  private const val MAX_CAUSE_DEPTH = 8
}

/**
 * Reports [error] through [report] (Crashlytics) unless it is a refusal: that is expected, so it
 * only leaves a debug log line.
 * @return the refusal, when it was one.
 */
fun reportUnlessPaERefusal(error: Throwable, report: (Throwable) -> Unit): PaERefusal? {
  val refusal = PaERefusals.from(error)
  if (refusal == null) {
    report(error)
  } else {
    Timber.d("Play & Earn refusal ${refusal.code}, retry at ${refusal.retryAt}")
  }
  return refusal
}

/** Shorthand for [PaERefusals.from]. */
fun Throwable.toPaERefusal(): PaERefusal? = PaERefusals.from(this)

private fun HttpException.peekErrorBody(): String? = try {
  // Peeked, not read: whoever reads the body next still gets all of it.
  response()?.errorBody()?.source()?.peek()?.let { peek ->
    peek.request(PaERefusals.MAX_BODY_BYTES)
    peek.readUtf8(minOf(peek.buffer.size, PaERefusals.MAX_BODY_BYTES))
  }
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
    // No zone given: the backend's times are UTC.
    ?: runCatching { LocalDateTime.parse(this).toInstant(ZoneOffset.UTC) }.getOrNull()
