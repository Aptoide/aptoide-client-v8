package com.aptoide.android.aptoidegames.gamegenie.data

import cm.aptoide.pt.aptoide_network.di.GameGenieOkHttp
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieCompanionRequest
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieRequest
import com.aptoide.android.aptoidegames.gamegenie.io_models.GenieAppRef
import com.aptoide.android.aptoidegames.gamegenie.io_models.GenieSseEvent
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Streaming consumer for `POST /api/genie` (general chat) and `POST /api/genie/companion`
 * (companion chat). Bypasses Retrofit to read the `text/event-stream` body line-by-line so
 * `delta` chunks surface to the UI as they arrive. Query params and `User-Agent` are added by
 * the [GameGenieOkHttp] interceptor chain.
 *
 * Read timeout is bumped to 5 minutes since LLM generations can exceed the default 30s.
 */
@Singleton
class GameGenieSseClient @Inject constructor(
  @GameGenieOkHttp baseClient: OkHttpClient,
) {

  private val streamingClient: OkHttpClient = baseClient.newBuilder()
    .readTimeout(STREAM_READ_TIMEOUT_MINUTES, TimeUnit.MINUTES)
    .build()

  private val gson = Gson()

  /**
   * Streams [GenieSseEvent]s for a single `/api/genie` POST (general chat).
   */
  fun stream(
    bearerToken: String,
    request: GameGenieRequest,
  ): Flow<GenieSseEvent> = streamFromPath(bearerToken, GENIE_PATH, request)

  /**
   * Streams [GenieSseEvent]s for a single `/api/genie/companion` POST.
   */
  fun streamCompanion(
    bearerToken: String,
    request: GameGenieCompanionRequest,
  ): Flow<GenieSseEvent> = streamFromPath(bearerToken, GENIE_COMPANION_PATH, request)

  /**
   * Shared SSE pipeline for both genie endpoints. Throws [HttpException] on non-2xx (so the
   * manager can refresh JWT on 401) and [IOException] on mid-stream socket/parse failure.
   * Cancels the underlying [okhttp3.Call] on flow cancellation.
   */
  private fun streamFromPath(
    bearerToken: String,
    path: String,
    request: Any,
  ): Flow<GenieSseEvent> = callbackFlow {
    val url = "${BuildConfig.GAME_GENIE_API}$path"
    val body = gson.toJson(request).toRequestBody("application/json".toMediaType())

    val httpRequest = Request.Builder()
      .url(url)
      .post(body)
      .addHeader("Authorization", bearerToken)
      .addHeader("Accept", "text/event-stream")
      .build()

    val call = streamingClient.newCall(httpRequest)

    // Read on a child IO coroutine so awaitClose can fire mid-generation if the user navigates
    // away — blocking I/O directly in the builder block would defer cancellation until EOF.
    launch(Dispatchers.IO) {
      try {
        call.execute().use { response ->
          val responseBody = response.body
          if (!response.isSuccessful) {
            // Mirror Retrofit so upstream 401 handling stays uniform.
            throw HttpException(Response.error<Any>(response.code, responseBody))
          }
          val source = responseBody.source()
          val buffer = StringBuilder()

          while (!source.exhausted()) {
            val line = source.readUtf8Line() ?: break
            if (line.isEmpty()) {
              val event = parseEventBlock(buffer.toString())
              buffer.setLength(0)
              if (event != null) {
                trySend(event)
                if (event is GenieSseEvent.Done || event is GenieSseEvent.Error) break
              }
            } else {
              buffer.append(line).append('\n')
            }
          }
          // Defensive flush in case the server omits the trailing `\n\n`.
          if (buffer.isNotEmpty()) {
            parseEventBlock(buffer.toString())?.let { trySend(it) }
          }
        }
        close()
      } catch (t: Throwable) {
        close(t)
      }
    }

    awaitClose {
      // Idempotent — avoids "Call cancelled" log spam on normal completion.
      if (!call.isCanceled()) call.cancel()
    }
  }

  // The server carries the event type inside the JSON payload (no `event:` field), so we only
  // look at the `data:` line. Unknown blocks return null so the caller keeps consuming.
  private fun parseEventBlock(block: String): GenieSseEvent? {
    val dataLine = block.lineSequence()
      .firstOrNull { it.startsWith("data:") }
      ?: return null
    val json = dataLine.removePrefix("data:").trim()
    if (json.isEmpty()) return null
    return runCatching { decode(json) }
      .onFailure { Timber.w(it, "Failed to parse genie SSE event: %s", json) }
      .getOrNull()
  }

  private fun decode(json: String): GenieSseEvent? {
    val obj = gson.fromJson(json, JsonObject::class.java) ?: return null
    return when (obj.get("type")?.asString) {
      "meta" -> GenieSseEvent.Meta(id = obj.get("id").asString)
      "delta" -> GenieSseEvent.Delta(text = obj.get("text")?.asString.orEmpty())
      "apps" -> GenieSseEvent.Apps(apps = parseAppRefs(obj))
      "video" -> GenieSseEvent.Video(
        videoId = obj.get("video")?.takeIf { !it.isJsonNull }?.asString
      )
      "follow_ups" -> GenieSseEvent.FollowUps(followUps = parseFollowUps(obj))
      "done" -> GenieSseEvent.Done(
        id = obj.get("id").asString,
        title = obj.get("title")?.takeIf { !it.isJsonNull }?.asString,
        apps = parseAppRefs(obj),
        video = obj.get("video")?.takeIf { !it.isJsonNull }?.asString,
        followUps = parseFollowUps(obj),
      )
      "error" -> GenieSseEvent.Error(
        message = obj.get("message")?.takeIf { !it.isJsonNull }?.asString
      )
      else -> null
    }
  }

  // Entries without `package` are skipped — they're unusable for repo resolution downstream.
  private fun parseAppRefs(obj: JsonObject): List<GenieAppRef> =
    obj.get("apps")?.asJsonArray?.mapNotNull { element ->
      val appObj = element.asJsonObject
      val pkg = appObj.get("package")?.takeIf { !it.isJsonNull }?.asString ?: return@mapNotNull null
      GenieAppRef(
        name = appObj.get("name")?.takeIf { !it.isJsonNull }?.asString,
        packageName = pkg,
      )
    }.orEmpty()

  // Cap at 3 to match the UI strip width.
  private fun parseFollowUps(obj: JsonObject): List<String> =
    obj.get("follow_ups")?.asJsonArray?.mapNotNull { el ->
      el.takeIf { it.isJsonPrimitive && !it.isJsonNull }?.asString
    }?.take(3).orEmpty()

  companion object {
    private const val GENIE_PATH = "api/genie"
    private const val GENIE_COMPANION_PATH = "api/genie/companion"
    private const val STREAM_READ_TIMEOUT_MINUTES = 5L
  }
}
