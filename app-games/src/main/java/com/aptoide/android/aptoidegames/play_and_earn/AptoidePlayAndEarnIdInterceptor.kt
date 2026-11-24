package com.aptoide.android.aptoidegames.play_and_earn

import cm.aptoide.pt.aptoide_network.data.network.PlayAndEarnIdInterceptor
import cm.aptoide.pt.environment_info.PaEDeviceIdentity
import com.aptoide.android.aptoidegames.device_info.PaENetworkState
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response

/**
 * Every Play & Earn call goes through this interceptor. It adds the user's `guest_id` and the
 * device evidence the backend's anti-fraud rules read. The app only reports; the backend decides.
 */
class AptoidePlayAndEarnIdInterceptor(
  private val guestIdSource: PaEGuestIdSource,
  private val deviceIdentity: PaEDeviceIdentity,
  private val networkState: PaENetworkState,
  private val distribution: String,
) : PlayAndEarnIdInterceptor {

  private companion object {
    const val ID_TIMEOUT_MS = 2_000L

    const val HEADER_DEVICE_KEY = "X-PaE-Device-Key"
    const val HEADER_INSTALL_ID = "X-PaE-Install-Id"
    const val HEADER_VPN = "X-PaE-Vpn"
    const val HEADER_PROXY = "X-PaE-Proxy"
    const val HEADER_DISTRIBUTION = "X-PaE-Distribution"
  }

  override fun intercept(chain: Chain): Response {
    val originalRequest = chain.request()

    // On timeout, each value is left out rather than stalling the call. The device key and the
    // install id are cached after the first read, so only the first request waits for them.
    val (guestId, deviceKey, installId) = runBlocking {
      Triple(
        withTimeoutOrNull(ID_TIMEOUT_MS) { guestIdSource.getId() }.orEmpty(),
        withTimeoutOrNull(ID_TIMEOUT_MS) { deviceIdentity.getDeviceKey() },
        withTimeoutOrNull(ID_TIMEOUT_MS) { deviceIdentity.getInstallId() },
      )
    }
    val network = networkState.read()

    val builder = originalRequest.newBuilder()
      .evidenceHeader(HEADER_DEVICE_KEY, deviceKey)
      .evidenceHeader(HEADER_INSTALL_ID, installId)
      .evidenceHeader(HEADER_VPN, network.vpnActive?.asFlag())
      .evidenceHeader(HEADER_PROXY, network.proxyEnabled?.asFlag())
      .evidenceHeader(HEADER_DISTRIBUTION, distribution)

    if (guestId.isNotBlank()) {
      builder.url(
        originalRequest.url.newBuilder()
          .addQueryParameter("guest_id", guestId)
          .build()
      )
    }

    return chain.proceed(builder.build())
  }

  // A value the app couldn't read is left out, never sent empty or as "0": the backend must be
  // able to tell "unknown" from "no".
  private fun Request.Builder.evidenceHeader(name: String, value: String?): Request.Builder =
    if (value.isNullOrBlank()) removeHeader(name) else header(name, value)

  private fun Boolean.asFlag(): String = if (this) "1" else "0"
}
