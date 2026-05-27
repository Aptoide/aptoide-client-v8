package com.aptoide.android.aptoidegames.play_and_earn.presentation.units

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.exception_handler.ExceptionHandler
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.wallet.datastore.WalletCoreDataSource
import cm.aptoide.pt.wallet.wallet_info.data.WalletInfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UnitsBalanceViewModel @Inject constructor(
  private val walletInfoRepository: WalletInfoRepository,
  private val walletCoreDataSource: WalletCoreDataSource,
  private val exceptionHandler: ExceptionHandler,
) : ViewModel() {

  private val _uiState = MutableStateFlow<UnitsBalanceUiState>(UnitsBalanceUiState.Loading)
  val uiState: StateFlow<UnitsBalanceUiState> = _uiState.asStateFlow()

  init {
    reload()
  }

  fun reload() {
    viewModelScope.launch {
      _uiState.update { UnitsBalanceUiState.Loading }
      runCatching {
        val walletAddress = walletCoreDataSource.getCurrentWalletAddress()
          ?: error("No wallet address available")
        walletInfoRepository.getWalletInfo(walletAddress)
      }.fold(
        onSuccess = { walletInfo ->
          _uiState.update {
            UnitsBalanceUiState.Idle(
              availableUnits = walletInfo.unitsBalance,
            )
          }
        },
        onFailure = { error ->
          exceptionHandler.recordException(error)
          _uiState.update {
            UnitsBalanceUiState.Error(
              message = error.message.orEmpty(),
              retry = ::reload,
            )
          }
        }
      )
    }
  }
}

@Composable
fun rememberUnitsBalanceUiState(): UnitsBalanceUiState = runPreviewable(
  preview = { UnitsBalanceUiStateProvider().values.toList().random() },
  real = {
    val vm = hiltViewModel<UnitsBalanceViewModel>()
    val uiState by vm.uiState.collectAsState()
    uiState
  }
)
