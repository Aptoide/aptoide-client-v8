package com.aptoide.android.aptoidegames.play_and_earn

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * App-wide signal to refresh the user's P&E unit balance after it changes — e.g. claiming a reward
 * or redeeming units. Call [invalidate] from any balance-changing action; observers of the balance
 * (such as the toolbar badge) re-fetch when [refreshSignal] changes.
 */
@Singleton
class WalletUnitsRefresher @Inject constructor() {

  private val _refreshSignal = MutableStateFlow(0)
  val refreshSignal: StateFlow<Int> = _refreshSignal.asStateFlow()

  fun invalidate() {
    _refreshSignal.update { it + 1 }
  }
}
