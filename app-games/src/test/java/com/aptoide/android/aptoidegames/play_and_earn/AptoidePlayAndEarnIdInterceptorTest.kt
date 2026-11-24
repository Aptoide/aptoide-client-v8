package com.aptoide.android.aptoidegames.play_and_earn

import cm.aptoide.pt.environment_info.PaEDeviceIdentity
import com.aptoide.android.aptoidegames.device_info.PaENetworkEvidence
import com.aptoide.android.aptoidegames.device_info.PaENetworkState
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class AptoidePlayAndEarnIdInterceptorTest {

  private class FakeIdentity(
    private val deviceKey: String? = DEVICE_KEY,
    private val installId: String = INSTALL_ID,
  ) : PaEDeviceIdentity {
    override suspend fun getDeviceKey(): String? = deviceKey
    override suspend fun getInstallId(): String = installId
  }

  /** Sends [request] through the interceptor and returns what reached the network. */
  private fun interceptedRequest(
    guestId: String = "guest-1",
    identity: PaEDeviceIdentity = FakeIdentity(),
    network: PaENetworkEvidence = PaENetworkEvidence(vpnActive = false, proxyEnabled = false),
    distribution: String = "direct",
    request: Request = Request.Builder().url("https://rewards.example.com/api/x").build(),
  ): Request {
    var sent: Request? = null
    val client = OkHttpClient.Builder()
      .addInterceptor(
        AptoidePlayAndEarnIdInterceptor(
          guestIdSource = { guestId },
          deviceIdentity = identity,
          networkState = PaENetworkState { network },
          distribution = distribution,
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
    val request = interceptedRequest(identity = FakeIdentity(deviceKey = null))

    assertNull(request.header("X-PaE-Device-Key"))
    assertEquals(INSTALL_ID, request.header("X-PaE-Install-Id"))
  }

  @Test
  fun `keeps adding guest_id to the query`() {
    val request = interceptedRequest(guestId = "guest-42")

    assertEquals("guest-42", request.url.queryParameter("guest_id"))
  }

  @ParameterizedTest
  @ValueSource(strings = ["", " "])
  fun `without a guest id the evidence headers are still sent`(guestId: String) {
    val request = interceptedRequest(guestId = guestId)

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

  private companion object {
    const val DEVICE_KEY = "4d853cd5e7d688e75b3dca9abc2fa52ac8bafb750b6d4dc01b78e2e7bb335f8f"
    const val INSTALL_ID = "7b0c3f0e-1d2a-4c5b-9e8f-0a1b2c3d4e5f"
  }
}
