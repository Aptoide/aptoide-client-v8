package com.aptoide.android.aptoidegames.play_and_earn.presentation.level_up

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.wallet.datastore.WalletCoreDataSource
import cm.aptoide.pt.wallet.wallet_info.data.WalletInfoRepository
import com.aptoide.android.aptoidegames.play_and_earn.WalletUnitsRefresher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject
import kotlin.random.Random
import kotlin.random.nextLong

@HiltViewModel
class WalletUnitsViewModel @Inject constructor(
  private val walletInfoRepository: WalletInfoRepository,
  walletCoreDataSource: WalletCoreDataSource,
  walletUnitsRefresher: WalletUnitsRefresher,
) : ViewModel() {

  private companion object {
    // After an invalidate the backend balance can lag (redeems take ~1-2s). Poll at these offsets
    // (ms from the action) until it changes, rather than trusting a single immediate fetch.
    val REFRESH_OFFSETS_MS = longArrayOf(1000L, 3000L, 5000L)
  }

  // Last balance we emitted; used to tell when a refresh has actually landed on the backend.
  private var lastBalance: Long? = null
  // Last refresh signal seen; lets us tell a balance-changing refresh from an initial/address load.
  private var lastSignal = 0

  @OptIn(ExperimentalCoroutinesApi::class)
  val uiState: StateFlow<Long?> = combine(
    walletCoreDataSource.observeCurrentWalletAddress().distinctUntilChanged(),
    // Re-fetch whenever the balance is invalidated (reward claimed, units redeemed, ...).
    walletUnitsRefresher.refreshSignal,
  ) { walletAddress, signal -> walletAddress to signal }
    .flatMapLatest { (walletAddress, signal) ->
      val isRefresh = signal != lastSignal
      lastSignal = signal
      if (walletAddress != null) {
        flow<Long?> {
          // Initial load / address change: fetch immediately. Invalidate: poll for the new value.
          emit(
            if (isRefresh) pollUpdatedUnits(walletAddress)
            else walletInfoRepository.getWalletInfo(walletAddress).unitsBalance
          )
        }
      } else {
        flowOf(null)
      }
    }
    .onEach { lastBalance = it }
    .catch { e ->
      Timber.w(e)
      emit(null)
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000L),
      initialValue = null
    )

  // Polls the balance at REFRESH_OFFSETS_MS (from the action), returning as soon as it differs from
  // the value we're showing, or after the final offset. Offsets are absolute, not cumulative.
  private suspend fun pollUpdatedUnits(walletAddress: String): Long {
    val previous = lastBalance
    var elapsed = 0L
    for (offset in REFRESH_OFFSETS_MS.dropLast(1)) {
      delay(offset - elapsed)
      elapsed = offset
      val units = walletInfoRepository.getWalletInfo(walletAddress).unitsBalance
      if (units != previous) return units
    }
    delay(REFRESH_OFFSETS_MS.last() - elapsed)
    return walletInfoRepository.getWalletInfo(walletAddress).unitsBalance
  }
}

@Composable
fun rememberWalletUnits(): Long? = runPreviewable(
  preview = { Random.nextLong(0L..200L) },
  real = {
    val vm = hiltViewModel<WalletUnitsViewModel>()
    val uiState by vm.uiState.collectAsState()
    uiState
  }
)
