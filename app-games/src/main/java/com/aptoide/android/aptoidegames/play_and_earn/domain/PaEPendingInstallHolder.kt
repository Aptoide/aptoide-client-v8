package com.aptoide.android.aptoidegames.play_and_earn.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remembers a Play & Earn install the user asked for while logged out (AND-876).
 *
 * A Play & Earn game may only be downloaded by a signed-in user, because the developer's MMP click
 * link must carry the wallet id. When a logged-out user taps Install, the tap is stored here, the
 * sign-in screen opens, and the install view that owns the package starts the download once the
 * user is back and signed in. There is at most one pending install at a time.
 */
@Singleton
class PaEPendingInstallHolder @Inject constructor() {

  data class PendingInstall(
    val packageName: String,
    val appName: String,
  )

  private val _pending = MutableStateFlow<PendingInstall?>(null)
  val pending: StateFlow<PendingInstall?> = _pending.asStateFlow()

  fun request(packageName: String, appName: String) {
    _pending.value = PendingInstall(packageName = packageName, appName = appName)
  }

  /** Clears and returns true if [packageName] is the pending install; false otherwise. */
  fun consume(packageName: String): Boolean {
    if (_pending.value?.packageName != packageName) return false
    _pending.value = null
    return true
  }

  fun clear() {
    _pending.value = null
  }
}
