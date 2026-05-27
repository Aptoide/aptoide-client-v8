package com.aptoide.android.aptoidegames.play_and_earn.presentation.units

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

sealed interface UnitsBalanceUiState {

  /**
   * @param availableUnits the current units balance of the user.
   */
  data class Idle(
    val availableUnits: Long,
  ) : UnitsBalanceUiState

  data object Loading : UnitsBalanceUiState

  data class Error(val message: String = "", val retry: () -> Unit) : UnitsBalanceUiState
}

class UnitsBalanceUiStateProvider : PreviewParameterProvider<UnitsBalanceUiState.Idle> {
  override val values: Sequence<UnitsBalanceUiState.Idle> = sequenceOf(
    UnitsBalanceUiState.Idle(availableUnits = 0L),
    UnitsBalanceUiState.Idle(availableUnits = 310L),
    UnitsBalanceUiState.Idle(availableUnits = 500L),
    UnitsBalanceUiState.Idle(availableUnits = 650L),
  )
}
