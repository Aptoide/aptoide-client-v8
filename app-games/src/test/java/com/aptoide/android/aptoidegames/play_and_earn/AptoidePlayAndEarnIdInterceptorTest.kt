package com.aptoide.android.aptoidegames.play_and_earn

import cm.aptoide.pt.environment_info.PaEDeviceIdentity
import com.aptoide.android.aptoidegames.device_info.PaENetworkEvidence
import com.aptoide.android.aptoidegames.device_info.PaENetworkState
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

internal class AptoidePlayAndEarnIdInterceptorTest {

  private class FakeIdentity(
    private val deviceKey: suspend () -> String? = { DEVICE_KEY },
    private val installId: suspend () -> String = { INSTALL_ID },
  ) : PaEDeviceIdentity {
    override suspend fun getDeviceKey(): String? = deviceKey()
    override suspend fun getInstallId(): String = installId()
  }

  /** Sends [request] through the interceptor and returns what reached the network. */
  private fun interceptedRequest(
    guestIdSource: PaEGuestIdSource = PaEGuestIdSource { "guest-1" },
    identity: PaEDeviceIdentity = FakeIdentity(),
    network: PaENetworkEvidence = PaENetworkEvidence(vpnActive = false, proxyEnabled = false),
    distribution: String = "direct",
    idTimeoutMs: Long = 2_000L,
    request: Request = Request.Builder().url("https://rewards.example.com/api/x").build(),
  ): Request {
    var sent: Request? = null
    val client = OkHttpClient.Builder()
      .addInterceptor(
        AptoidePlayAndEarnIdInterceptor(
          guestIdSource = guestIdSource,
          deviceIdentity = identity,
          networkState = PaENetworkState { network },
          distribution = distribution,
          idTimeoutMs = idTimeoutMs,
        )
      )
      .addInterceptor { chain ->
        sent = chain.request()
        Response.Builder()
          .request(chain.request())
          .protocol(Protocol.HTTP_1_1)
          .code(200)
          .message("OK")
          .body("".toResponseBody())
          .build()
      }
      .build()
    client.newCall(request).execute().close()
    return sent!!
  }

  @Test
  fun `adds the five evidence headers`() {
    val request = interceptedRequest(
      network = PaENetworkEvidence(vpnActive = true, proxyEnabled = false),
      distribution = "gplay",
    )

    assertEquals(DEVICE_KEY, request.header("X-PaE-Device-Key"))
    assertEquals(INSTALL_ID, request.header("X-PaE-Install-Id"))
    assertEquals("1", request.header("X-PaE-Vpn"))
    assertEquals("0", request.header("X-PaE-Proxy"))
    assertEquals("gplay", request.header("X-PaE-Distribution"))
  }

  @Test
  fun `proxy on and vpn off are sent as 1 and 0`() {
    val request = interceptedRequest(
      network = PaENetworkEvidence(vpnActive = false, proxyEnabled = true),
    )

    assertEquals("0", request.header("X-PaE-Vpn"))
    assertEquals("1", request.header("X-PaE-Proxy"))
    assertEquals("direct", request.header("X-PaE-Distribution"))
  }

  @Test
  fun `vpn and proxy headers are left out when the read fails`() {
    val request = interceptedRequest(
      network = PaENetworkEvidence(vpnActive = null, proxyEnabled = null),
    )

    assertNull(request.header("X-PaE-Vpn"))
    assertNull(request.header("X-PaE-Proxy"))
    assertEquals(DEVICE_KEY, request.header("X-PaE-Device-Key"))
  }

  @Test
  fun `device key header is left out when the Android ID is unavailable`() {
    val request = interceptedRequest(identity = FakeIdentity(deviceKey = { null }))

    assertNull(request.header("X-PaE-Device-Key"))
    assertEquals(INSTALL_ID, request.header("X-PaE-Install-Id"))
  }

  @Test
  fun `keeps adding guest_id to the query`() {
    val request = interceptedRequest(guestIdSource = { "guest-42" })

    assertEquals("guest-42", request.url.queryParameter("guest_id"))
  }

  @ParameterizedTest
  @ValueSource(strings = ["", " "])
  fun `without a guest id the evidence headers are still sent`(guestId: String) {
    val request = interceptedRequest(guestIdSource = { guestId })

    assertNull(request.url.queryParameter("guest_id"))
    assertEquals(DEVICE_KEY, request.header("X-PaE-Device-Key"))
    assertEquals(INSTALL_ID, request.header("X-PaE-Install-Id"))
    assertEquals("direct", request.header("X-PaE-Distribution"))
  }

  @Test
  fun `keeps the request's other headers and replaces a spoofed evidence header`() {
    val request = interceptedRequest(
      request = Request.Builder()
        .url("https://rewards.example.com/api/x")
        .header("Authorization", "Bearer t")
        .header("X-PaE-Device-Key", "spoofed")
        .build()
    )

    assertEquals("Bearer t", request.header("Authorization"))
    assertEquals(listOf(DEVICE_KEY), request.headers("X-PaE-Device-Key"))
  }

  @Test
  fun `an install id read that throws leaves only that header out`() {
    val request = interceptedRequest(
      identity = FakeIdentity(installId = { throw IOException("datastore broken") })
    )

    assertNull(request.header("X-PaE-Install-Id"))
    assertEquals(DEVICE_KEY, request.header("X-PaE-Device-Key"))
    assertEquals("0", request.header("X-PaE-Vpn"))
    assertEquals("0", request.header("X-PaE-Proxy"))
    assertEquals("direct", request.header("X-PaE-Distribution"))
    assertEquals("guest-1", request.url.queryParameter("guest_id"))
  }

  @Test
  fun `a device key read that throws leaves only that header out`() {
    val request = interceptedRequest(
      identity = FakeIdentity(deviceKey = { throw IllegalStateException("boom") })
    )

    assertNull(request.header("X-PaE-Device-Key"))
    assertEquals(INSTALL_ID, request.header("X-PaE-Install-Id"))
  }

  @Test
  fun `a guest id read that throws leaves guest_id out and keeps the headers`() {
    val request = interceptedRequest(guestIdSource = { throw IOException("no firebase") })

    assertNull(request.url.queryParameter("guest_id"))
    assertEquals(DEVICE_KEY, request.header("X-PaE-Device-Key"))
    assertEquals(INSTALL_ID, request.header("X-PaE-Install-Id"))
  }

  @Test
  fun `a source that throws CancellationException does not fail the request`() {
    val request = interceptedRequest(
      guestIdSource = { throw CancellationException("firebase task cancelled") }
    )

    assertNull(request.url.queryParameter("guest_id"))
    assertEquals(INSTALL_ID, request.header("X-PaE-Install-Id"))
  }

  @Test
  fun `values not ready within the shared budget are left out and the request proceeds`() {
    val started = System.nanoTime()

    val request = interceptedRequest(
      guestIdSource = { awaitCancellation() },
      identity = FakeIdentity(
        deviceKey = { DEVICE_KEY },
        installId = { awaitCancellation() },
      ),
      idTimeoutMs = 100L,
    )

    val elapsedMs = (System.nanoTime() - started) / 1_000_000
    assertNull(request.url.queryParameter("guest_id"))
    assertNull(request.header("X-PaE-Install-Id"))
    assertEquals(DEVICE_KEY, request.header("X-PaE-Device-Key"))
    assertEquals("direct", request.header("X-PaE-Distribution"))
    assertTrue(elapsedMs < 1_000, "waited $elapsedMs ms")
  }

  @Test
  fun `the budget is shared, not spent once per value`() {
    interceptedRequest() // warm up OkHttp so the measurement below is the interceptor's wait
    val started = System.nanoTime()

    val request = interceptedRequest(
      guestIdSource = { delay(400); "guest-slow" },
      identity = FakeIdentity(
        deviceKey = { delay(400); DEVICE_KEY },
        installId = { delay(400); INSTALL_ID },
      ),
      idTimeoutMs = 700L,
    )

    // One after the other these would need 1200 ms; under one 700 ms budget they all fit only
    // when read together.
    val elapsedMs = (System.nanoTime() - started) / 1_000_000
    assertEquals("guest-slow", request.url.queryParameter("guest_id"))
    assertEquals(DEVICE_KEY, request.header("X-PaE-Device-Key"))
    assertEquals(INSTALL_ID, request.header("X-PaE-Install-Id"))
    assertTrue(elapsedMs < 1_000, "waited $elapsedMs ms")
  }

  @Test
  fun `an unknown value strips a caller-supplied header of that name`() {
    val request = interceptedRequest(
      identity = FakeIdentity(deviceKey = { null }),
      network = PaENetworkEvidence(vpnActive = null, proxyEnabled = null),
      request = Request.Builder()
        .url("https://rewards.example.com/api/x")
        .header("X-PaE-Device-Key", "spoofed")
        .header("X-PaE-Vpn", "0")
        .header("X-PaE-Proxy", "0")
        .build()
    )

    assertNull(request.header("X-PaE-Device-Key"))
    assertNull(request.header("X-PaE-Vpn"))
    assertNull(request.header("X-PaE-Proxy"))
  }

  private companion object {
    const val DEVICE_KEY = "4d853cd5e7d688e75b3dca9abc2fa52ac8bafb750b6d4dc01b78e2e7bb335f8f"
    const val INSTALL_ID = "7b0c3f0e-1d2a-4c5b-9e8f-0a1b2c3d4e5f"
  }
}
