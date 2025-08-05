package cm.aptoide.pt.play_and_earn.exchange.presentation

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import kotlin.random.Random
import kotlin.random.nextLong

sealed interface ExchangeUiState {

  data class Idle(val availableUnits: Long, val exchangeUnits: () -> Unit) : ExchangeUiState

  data object Loading : ExchangeUiState

  data class Success(val availableUnits: Long) : ExchangeUiState

  data class Error(val message: String = "", val retry: () -> Unit) : ExchangeUiState
}

class ExchangeUiStateProvider : PreviewParameterProvider<ExchangeUiState> {
  override val values: Sequence<ExchangeUiState> = sequenceOf(
    ExchangeUiState.Idle(Random.nextLong(0L..300L), {}),
    ExchangeUiState.Loading,
    ExchangeUiState.Success(Random.nextLong(0L..300L)),
    ExchangeUiState.Error(retry = {})
  )
}
