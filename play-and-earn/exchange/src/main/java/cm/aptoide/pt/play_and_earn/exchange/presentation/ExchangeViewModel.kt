package cm.aptoide.pt.play_and_earn.exchange.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.exception_handler.ExceptionHandler
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.play_and_earn.exchange.domain.ExchangeUnitsUseCase
import cm.aptoide.pt.wallet.wallet_info.domain.GetWalletUnitsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExchangeViewModel @Inject constructor(
  private val exchangeUnitsUseCase: ExchangeUnitsUseCase,
  private val getWalletUnitsUseCase: GetWalletUnitsUseCase,
  private val exceptionHandler: ExceptionHandler
) : ViewModel() {

  private val _uiState = MutableStateFlow<ExchangeUiState>(ExchangeUiState.Loading)
  val uiState: StateFlow<ExchangeUiState> = _uiState.asStateFlow()

  private var availableUnits: Long? = null

  init {
    reload()
  }

  fun reload() {
    viewModelScope.launch {
      getWalletUnitsUseCase()?.let { units ->
        availableUnits = units
        _uiState.update { ExchangeUiState.Idle(units, ::exchangeUnits) }
      } ?: _uiState.update { ExchangeUiState.Error(message = "", retry = ::reload) }
    }
  }

  private fun exchangeUnits() {
    viewModelScope.launch {
      _uiState.update { ExchangeUiState.Loading }

      exchangeUnitsUseCase().fold(
        onSuccess = {
          _uiState.update { ExchangeUiState.Success(availableUnits = availableUnits!!) }
        },
        onFailure = { error ->
          exceptionHandler.recordException(error)
          
          _uiState.update {
            ExchangeUiState.Error(
              message = error.message ?: "",
              retry = ::exchangeUnits
            )
          }
        }
      )
    }
  }
}

@Composable
fun rememberExchangeUiState(): ExchangeUiState = runPreviewable(
  preview = { ExchangeUiStateProvider().values.toSet().random() },
  real = {
    val vm = hiltViewModel<ExchangeViewModel>()

    val uiState by vm.uiState.collectAsState()
    uiState
  }
)
