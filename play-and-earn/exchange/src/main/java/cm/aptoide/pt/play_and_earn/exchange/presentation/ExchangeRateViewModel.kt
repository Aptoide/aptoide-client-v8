package cm.aptoide.pt.play_and_earn.exchange.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.exception_handler.ExceptionHandler
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.play_and_earn.exchange.domain.ExchangeRate
import cm.aptoide.pt.play_and_earn.exchange.domain.GetExchangeRateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ExchangeRateViewModel @Inject constructor(
  private val getExchangeRateUseCase: GetExchangeRateUseCase,
  private val exceptionHandler: ExceptionHandler
) : ViewModel() {

  private val _exchangeRate = MutableStateFlow<ExchangeRate?>(null)
  val exchangeRate: StateFlow<ExchangeRate?> = _exchangeRate.asStateFlow()

  init {
    reload()
  }

  fun reload() {
    viewModelScope.launch {
      getExchangeRateUseCase().fold(
        onSuccess = { rate -> _exchangeRate.update { rate } },
        onFailure = { error -> exceptionHandler.recordException(error) }
      )
    }
  }
}

// Returns how much money the given [units] are worth, or null until the rate is available.
@Composable
fun rememberExchangeRate(units: Long): BigDecimal? = runPreviewable(
  preview = { ExchangeRate(countryCode = "US", rate = 1.0, creditsAmount = 100).moneyValueOf(units) },
  real = {
    val vm = hiltViewModel<ExchangeRateViewModel>()

    val exchangeRate by vm.exchangeRate.collectAsState()
    exchangeRate?.moneyValueOf(units)
  }
)
