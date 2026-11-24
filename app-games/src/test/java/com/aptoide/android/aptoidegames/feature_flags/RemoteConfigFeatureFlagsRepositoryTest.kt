package com.aptoide.android.aptoidegames.feature_flags

import cm.aptoide.pt.test.gherkin.coScenario
import com.aptoide.android.aptoidegames.ads.native_ads.RecordingAnalytics
import com.aptoide.android.aptoidegames.feature_flags.analytics.FeatureFlagsAnalytics
import com.google.gson.Gson
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException

@ExperimentalCoroutinesApi
class RemoteConfigFeatureFlagsRepositoryTest {

  @Test
  fun `The fetch identifies the brand, the version and the install`() = coScenario {
    m Given "a repository for the Aptoide Games variant"
    val api = FakeConfigApi { response("""{"template_version":3,"values":{}}""") }
    val repository = repository(api = api)

    m When "the flags are fetched"
    repository.getFeatureFlags()

    m Then "the three parameters the server resolves on are sent"
    assertEquals(listOf(Triple("aptoide-games", 700, BUCKET_ID)), api.calls)
  }

  @Test
  fun `Values arrive as the strings the flags domain reads`() = coScenario {
    m Given "a response with a boolean, a number and a JSON document, all as strings"
    val body = """
      {"template_version":3,"values":{
        "show_home_tabs":"true",
        "pae_heartbeat_interval_seconds":"40",
        "home_tabs":"{\"tabs\":[{\"id\":\"ForYou\"}]}"
      }}
    """.trimIndent()
    val repository = repository(api = FakeConfigApi { response(body) })

    m When "the flags are fetched"
    val flags = repository.getFeatureFlags()

    m Then "each value is the exact string that was served"
    assertTrue(flags.getBoolean("show_home_tabs"))
    assertEquals("40", flags.get("pae_heartbeat_interval_seconds"))
    assertEquals("ForYou",
      JSONObject(flags.getString("home_tabs")).getJSONArray("tabs").getJSONObject(0).getString("id"))
  }

  @Test
  fun `A value that is not a string is still kept as its text`() = coScenario {
    m Given "a response where the server sent native JSON instead of strings"
    val body = """{"values":{"flag":true,"count":3,"doc":{"a":1},"gone":null}}"""
    val repository = repository(api = FakeConfigApi { response(body) })

    m When "the flags are fetched"
    val flags = repository.getFeatureFlags()

    m Then "primitives and documents are stored as text and a null is left out"
    assertEquals("true", flags.get("flag"))
    assertEquals("3", flags.get("count"))
    assertEquals(1, JSONObject(flags.getString("doc")).getInt("a"))
    assertFalse(flags.has("gone"))
  }

  @Test
  fun `A successful fetch reports its duration`() = coScenario {
    m Given "a fetch that takes 120 ms"
    val analytics = RecordingAnalytics()
    val clock = FakeClock(step = 120)
    val repository = repository(
      api = FakeConfigApi { response("""{"values":{"a":"b"}}""") },
      analytics = analytics,
      clock = clock,
    )

    m When "the flags are fetched"
    repository.getFeatureFlags()

    m Then "only the fetch event is sent, with the duration"
    assertEquals(listOf("feature_flags_fetch" to mapOf<String, Any?>("duration" to 120L)),
      analytics.events)
  }

  @ParameterizedTest
  @ValueSource(ints = [404, 422, 503])
  fun `An HTTP error reports its status and is rethrown`(status: Int) = coScenario {
    m Given "an endpoint answering with a problem response"
    val analytics = RecordingAnalytics()
    val error = HttpException(Response.error<ConfigResponse>(status, "{}".toResponseBody()))
    val repository = repository(
      api = FakeConfigApi { throw error },
      analytics = analytics,
      clock = FakeClock(step = 50),
    )

    m When "the flags are fetched"
    val thrown = runCatching { repository.getFeatureFlags() }.exceptionOrNull()

    m Then "the same error reaches the caller so the snapshot fallback runs"
    assertSame(error, thrown)
    m And "the failure event carries the HTTP status"
    assertEquals(
      listOf(
        "feature_flags_fetch_failure" to mapOf<String, Any?>(
          "duration" to 50L,
          "error_type" to "http",
          "error_http_code" to status,
        )
      ),
      analytics.events,
    )
  }

  @Test
  fun `A network error reports the exception kind and is rethrown`() = coScenario {
    m Given "a device with no connectivity"
    val analytics = RecordingAnalytics()
    val error = UnknownHostException("api.aptoide.com")
    val repository = repository(api = FakeConfigApi { throw error }, analytics = analytics)

    m When "the flags are fetched"
    val thrown = runCatching { repository.getFeatureFlags() }.exceptionOrNull()

    m Then "the same error reaches the caller"
    assertSame(error, thrown)
    m And "the failure event names the exception and has no HTTP status"
    val (name, params) = analytics.events.single()
    assertEquals("feature_flags_fetch_failure", name)
    assertEquals("UnknownHostException", params?.get("error_type"))
    assertNull(params?.get("error_http_code"))
  }

  @Test
  fun `A response without values is a failure, never an empty snapshot`() = coScenario {
    m Given "a 200 response that carries no values object"
    val analytics = RecordingAnalytics()
    val repository = repository(
      api = FakeConfigApi { response("""{"template_version":3}""") },
      analytics = analytics,
    )

    m When "the flags are fetched"
    val thrown = runCatching { repository.getFeatureFlags() }.exceptionOrNull()

    m Then "the fetch fails instead of returning no flags"
    assertTrue(thrown is MissingConfigValuesException)
    m And "the failure is reported under its own kind"
    assertEquals("MissingConfigValuesException",
      analytics.events.single().second?.get("error_type"))
  }

  @Test
  fun `A cancelled fetch is not reported as a failure`() = coScenario {
    m Given "a fetch that gets cancelled"
    val analytics = RecordingAnalytics()
    val repository = repository(
      api = FakeConfigApi { throw CancellationException("scope gone") },
      analytics = analytics,
    )

    m When "the flags are fetched"
    val thrown = runCatching { repository.getFeatureFlags() }.exceptionOrNull()

    m Then "the cancellation propagates and nothing is logged"
    assertTrue(thrown is CancellationException)
    assertTrue(analytics.events.isEmpty())
  }

  private fun repository(
    api: ConfigApi,
    analytics: RecordingAnalytics = RecordingAnalytics(),
    clock: FakeClock = FakeClock(step = 1),
  ) = RemoteConfigFeatureFlagsRepository(
    configApi = api,
    variant = "aptoide-games",
    versionCode = 700,
    bucketId = { BUCKET_ID },
    featureFlagsAnalytics = FeatureFlagsAnalytics(analytics.generic()),
    now = clock::next,
  )

  private fun response(json: String): ConfigResponse =
    Gson().fromJson(json, ConfigResponse::class.java)

  private class FakeConfigApi(private val answer: () -> ConfigResponse) : ConfigApi {
    val calls = mutableListOf<Triple<String, Int, String>>()

    override suspend fun getConfig(variant: String, versionCode: Int, bucketId: String): ConfigResponse {
      calls += Triple(variant, versionCode, bucketId)
      return answer()
    }
  }

  /** Advances by [step] on every read, so a fetch between two reads lasts exactly [step] ms */
  private class FakeClock(private val step: Long) {
    private var time = 1_000L
    fun next(): Long = time.also { time += step }
  }

  private companion object {
    const val BUCKET_ID = "550e8400-e29b-41d4-a716-446655440000"
  }
}
