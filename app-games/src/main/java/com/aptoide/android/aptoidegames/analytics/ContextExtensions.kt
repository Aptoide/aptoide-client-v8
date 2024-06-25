package com.aptoide.android.aptoidegames.analytics

import android.content.Context
import cm.aptoide.pt.install_manager.environment.NetworkConnection.State
import cm.aptoide.pt.network_listener.networkState

fun Context.getNetworkType() = when (this.networkState) {
  State.UNMETERED -> "wifi"
  State.METERED -> "mobile"
  State.GONE -> "off"
}
