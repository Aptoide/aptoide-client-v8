package com.aptoide.android.aptoidegames.play_and_earn.presentation.units

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.play_and_earn.exchange.domain.UNITS_EXCHANGE_THRESHOLD
import com.aptoide.android.aptoidegames.play_and_earn.data.PaEPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class UnitsExchangeThresholdViewModel @Inject constructor(
  paEPreferencesRepository: PaEPreferencesRepository,
) : ViewModel() {

  // Backed by /api/client/config (redeem_units_amount); UNITS_EXCHANGE_THRESHOLD until/if it loads.
  val threshold: StateFlow<Long> = paEPreferencesRepository.observeRedeemUnitsAmount()
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000L),
      initialValue = UNITS_EXCHANGE_THRESHOLD,
    )
}

@Composable
fun rememberUnitsExchangeThreshold(): Long = runPreviewable(
  preview = { UNITS_EXCHANGE_THRESHOLD },
  real = {
    val vm = hiltViewModel<UnitsExchangeThresholdViewModel>()
    val threshold by vm.threshold.collectAsState()
    threshold
  }
)
