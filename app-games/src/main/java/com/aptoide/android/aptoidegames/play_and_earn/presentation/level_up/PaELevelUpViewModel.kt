package com.aptoide.android.aptoidegames.play_and_earn.presentation.level_up

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.wallet.datastore.WalletCoreDataSource
import cm.aptoide.pt.wallet.gamification.data.GamificationRepository
import cm.aptoide.pt.wallet.gamification.domain.GamificationLevelStatus
import cm.aptoide.pt.wallet.wallet_info.data.WalletInfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PaELevelUpViewModel @Inject constructor(
  private val gamificationRepository: GamificationRepository,
  private val walletInfoRepository: WalletInfoRepository,
  private val walletCoreDataSource: WalletCoreDataSource,
) : ViewModel() {

  private companion object {
    const val DEFAULT_CURRENCY = "APPC"
  }

  private val viewModelState = MutableStateFlow<LevelUpUiState>(LevelUpUiState.Loading)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    reload()
  }

  fun reload() {
    viewModelScope.launch {
      viewModelState.update { LevelUpUiState.Loading }
      try {
        val walletAddress = walletCoreDataSource.getCurrentWalletAddress()

        if (walletAddress != null) {
          var gamificationStats =
            gamificationRepository.getGamificationStats(walletAddress, currency = DEFAULT_CURRENCY)
              .getOrThrow()
          val levels = gamificationRepository.getLevels(currency = DEFAULT_CURRENCY).getOrThrow()
          val walletInfo = walletInfoRepository.getWalletInfo(walletAddress)

          if (gamificationStats.gamificationStatus == GamificationLevelStatus.NONE) {
            gamificationStats = gamificationStats.copy(nextLevelAmount = levels.levelList[1].amount)
          }

          viewModelState.update {
            LevelUpUiState.Idle(
              walletInfo = walletInfo,
              gamificationStats = gamificationStats,
              levels = levels
            )
          }
        } else {
          viewModelState.update { LevelUpUiState.Error }
        }
      } catch (e: Throwable) {
        Timber.w(e)
        viewModelState.update {
          when (e) {
            is IOException -> LevelUpUiState.NoConnection
            else -> LevelUpUiState.Error
          }
        }
      }
    }
  }
}

@Composable
fun rememberLevelUpState(): Pair<LevelUpUiState, () -> Unit> {
  val vm = hiltViewModel<PaELevelUpViewModel>()
  val uiState by vm.uiState.collectAsState()
  return uiState to vm::reload
}
