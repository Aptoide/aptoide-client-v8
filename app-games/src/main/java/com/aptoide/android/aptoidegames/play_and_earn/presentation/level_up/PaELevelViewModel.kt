package com.aptoide.android.aptoidegames.play_and_earn.presentation.level_up

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.wallet.datastore.WalletCoreDataSource
import cm.aptoide.pt.wallet.gamification.data.GamificationRepository
import cm.aptoide.pt.wallet.gamification.domain.previewLevels
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

@HiltViewModel
class PaELevelViewModel @Inject constructor(
  private val gamificationRepository: GamificationRepository,
  walletCoreDataSource: WalletCoreDataSource,
) : ViewModel() {

  @OptIn(ExperimentalCoroutinesApi::class)
  val uiState: StateFlow<Int?> = walletCoreDataSource.observeCurrentWalletAddress()
    .distinctUntilChanged()
    .flatMapLatest { walletAddress ->
      if (walletAddress != null) {
        flow<Int?> {
          val level =
            gamificationRepository.getGamificationStats(walletAddress).getOrThrow().level
          emit(level)
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
fun rememberCurrentPaELevel(): Int? = runPreviewable(
  preview = { previewLevels.levelList.random().level },
  real = {
    val vm = hiltViewModel<PaELevelViewModel>()
    val uiState by vm.uiState.collectAsState()
    uiState
  }
)
