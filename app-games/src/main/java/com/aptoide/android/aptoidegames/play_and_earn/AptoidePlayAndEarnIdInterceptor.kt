package com.aptoide.android.aptoidegames.play_and_earn

import cm.aptoide.pt.aptoide_network.data.network.PlayAndEarnIdInterceptor
import cm.aptoide.pt.environment_info.PaEDeviceIdentity
import com.aptoide.android.aptoidegames.device_info.PaENetworkState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.joinAll
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
  private val idTimeoutMs: Long = ID_TIMEOUT_MS,
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

    val (guestId, deviceKey, installId) = readIds()
    val network = networkState.read()

    val builder = originalRequest.newBuilder()
      .evidenceHeader(HEADER_DEVICE_KEY, deviceKey)
      .evidenceHeader(HEADER_INSTALL_ID, installId)
      .evidenceHeader(HEADER_VPN, network.vpnActive?.asFlag())
      .evidenceHeader(HEADER_PROXY, network.proxyEnabled?.asFlag())
      .evidenceHeader(HEADER_DISTRIBUTION, distribution)

    if (!guestId.isNullOrBlank()) {
      builder.url(
        originalRequest.url.newBuilder()
          .addQueryParameter("guest_id", guestId)
          .build()
      )
    }

    return chain.proceed(builder.build())
  }

  /**
   * Reads the three ids together under one shared [idTimeoutMs] budget. A value that fails or
   * isn't ready in time is null and left out; nothing here can fail the request. The device key
   * and the install id are cached after the first read, so normally only the first call waits.
   */
  private fun readIds(): Triple<String?, String?, String?> = runBlocking {
    val guestId = async { readOrNull { guestIdSource.getId() } }
    val deviceKey = async { readOrNull { deviceIdentity.getDeviceKey() } }
    val installId = async { readOrNull { deviceIdentity.getInstallId() } }
    val reads = listOf(guestId, deviceKey, installId)

    withTimeoutOrNull(idTimeoutMs) { reads.joinAll() }
    reads.forEach { if (!it.isCompleted) it.cancel() }

    Triple(guestId.valueOrNull(), deviceKey.valueOrNull(), installId.valueOrNull())
  }

  // A cancelled read (our own timeout, or a source that throws CancellationException) ends
  // with no value; any other failure also yields no value.
  private suspend fun <T> readOrNull(read: suspend () -> T?): T? =
    try {
      read()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      null
    }

  @OptIn(ExperimentalCoroutinesApi::class)
  private fun <T> Deferred<T?>.valueOrNull(): T? =
    if (isCompleted && !isCancelled) getCompleted() else null

  // A value the app couldn't read is left out, never sent empty or as "0": the backend must be
  // able to tell "unknown" from "no".
  private fun Request.Builder.evidenceHeader(name: String, value: String?): Request.Builder =
    if (value.isNullOrBlank()) removeHeader(name) else header(name, value)

  private fun Boolean.asFlag(): String = if (this) "1" else "0"
}
