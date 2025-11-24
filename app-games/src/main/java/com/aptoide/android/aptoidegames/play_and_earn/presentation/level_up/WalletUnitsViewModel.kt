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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject
import kotlin.random.Random
import kotlin.random.nextLong

@HiltViewModel
class WalletUnitsViewModel @Inject constructor(
  private val walletInfoRepository: WalletInfoRepository,
  walletCoreDataSource: WalletCoreDataSource,
) : ViewModel() {

  @OptIn(ExperimentalCoroutinesApi::class)
  val uiState: StateFlow<Long?> = walletCoreDataSource.observeCurrentWalletAddress()
    .distinctUntilChanged()
    .flatMapLatest { walletAddress ->
      if (walletAddress != null) {
        flow<Long?> {
          val units = walletInfoRepository.getWalletInfo(walletAddress).unitsBalance
          emit(units)
        }
      } else {
        flowOf(null)
      }
    }
    .catch { e ->
      Timber.w(e)
      emit(null)
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000L),
      initialValue = null
    )
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
