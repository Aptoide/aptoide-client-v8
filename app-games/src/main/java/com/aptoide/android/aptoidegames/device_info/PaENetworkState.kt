package com.aptoide.android.aptoidegames.device_info

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * What the phone says about its network, sent to the Play & Earn backend as evidence.
 * Each value is null when it couldn't be read; the backend is then told nothing, never "off".
 */
data class PaENetworkEvidence(
  val vpnActive: Boolean?,
  val proxyEnabled: Boolean?,
)

fun interface PaENetworkState {
  /** Reads the current state. Cheap (two system calls), so it can run on every request. */
  fun read(): PaENetworkEvidence
}

class ConnectivityPaENetworkState(context: Context) : PaENetworkState {

  private val appContext = context.applicationContext

  override fun read(): PaENetworkEvidence {
    val connectivityManager = runCatching {
      appContext.getSystemService(ConnectivityManager::class.java)
    }.getOrNull()
    val network = runCatching { connectivityManager?.activeNetwork }.getOrNull()
    if (connectivityManager == null || network == null) {
      return PaENetworkEvidence(vpnActive = null, proxyEnabled = null)
    }

    val vpnActive = runCatching {
      connectivityManager.getNetworkCapabilities(network)
        ?.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
    }.getOrNull()

    // The active network's proxy, or failing that the global one the system applies to it.
    val proxyEnabled = runCatching {
      connectivityManager.getLinkProperties(network)?.let { linkProperties ->
        linkProperties.httpProxy != null || connectivityManager.defaultProxy != null
      }
    }.getOrNull()

    return PaENetworkEvidence(vpnActive = vpnActive, proxyEnabled = proxyEnabled)
  }
}
