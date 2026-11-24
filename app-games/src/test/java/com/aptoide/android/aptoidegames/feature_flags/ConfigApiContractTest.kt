package com.aptoide.android.aptoidegames.feature_flags

import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** The wire contract of the config endpoint, through the same Retrofit + Gson stack as the app */
@ExperimentalCoroutinesApi
class ConfigApiContractTest {

  private val server = MockWebServer()
  private lateinit var api: ConfigApi

  @BeforeEach
  fun setUp() {
    server.start()
    api = Retrofit.Builder()
      .baseUrl(server.url("/"))
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(ConfigApi::class.java)
  }

  @AfterEach
  fun tearDown() = server.shutdown()

  @Test
  fun `The request carries the variant, version code and bucket id and no country`() =
    coScenario {
      m Given "an endpoint that answers"
      server.enqueue(MockResponse().setBody("""{"template_version":3,"values":{"a":"b"}}"""))

      m When "the config is requested"
      api.getConfig(variant = "aptoide", versionCode = 13000, bucketId = "abc-123")

      m Then "the path and the three query parameters match the endpoint"
      val url = server.takeRequest().requestUrl!!
      assertEquals("/android/v1/config", url.encodedPath)
      assertEquals(setOf("variant", "version_code", "bucket_id"), url.queryParameterNames)
      assertEquals("aptoide", url.queryParameter("variant"))
      assertEquals("13000", url.queryParameter("version_code"))
      assertEquals("abc-123", url.queryParameter("bucket_id"))
      m And "the country is left to the edge"
      assertNull(url.queryParameter("country"))
    }

  @Test
  fun `The body is read into the template version and the values`() = coScenario {
    m Given "a response in the shape the endpoint serves"
    server.enqueue(
      MockResponse().setBody(
        """{"template_version":5,"values":{"show_home_tabs":"true","home_tabs":"{\"tabs\":[]}"}}"""
      )
    )

    m When "the config is requested"
    val response = api.getConfig(variant = "aptoide-games", versionCode = 700, bucketId = "abc")

    m Then "the values keep the served strings"
    assertEquals(5, response.templateVersion)
    assertEquals("true", response.values?.get("show_home_tabs")?.asString)
    assertEquals("""{"tabs":[]}""", response.values?.get("home_tabs")?.asString)
  }

  @ParameterizedTest
  @ValueSource(ints = [404, 422, 503])
  fun `A problem response surfaces as an HTTP error with its status`(status: Int) = coScenario {
    m Given "an endpoint answering with problem+json"
    server.enqueue(
      MockResponse()
        .setResponseCode(status)
        .setHeader("Content-Type", "application/problem+json")
        .setBody("""{"type":"about:blank","status":$status}""")
    )

    m When "the config is requested"
    val thrown = runCatching {
      api.getConfig(variant = "aptoide", versionCode = 700, bucketId = "abc")
    }.exceptionOrNull()

    m Then "the status is available to the failure report"
    assertEquals(status, (thrown as HttpException).code())
  }
}
