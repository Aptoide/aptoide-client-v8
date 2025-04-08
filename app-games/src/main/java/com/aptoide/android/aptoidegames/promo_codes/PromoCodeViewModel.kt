package com.aptoide.android.aptoidegames.promo_codes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.extensions.runPreviewable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PromoCodeViewModel @Inject constructor(
  private val promoCodeRepository: PromoCodeRepository,
) : ViewModel() {

  val uiState = promoCodeRepository.promoCode()
    .map { it to { promoCodeRepository.setPromoCode(null) } }
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      (null to {})
    )
}

@Composable
fun rememberPromoCodeApp() = runPreviewable(
  preview = { null to {} },
  real = {
    val vm = hiltViewModel<PromoCodeViewModel>()
    val promoCodeApp by vm.uiState.collectAsState(initial = null to {})
    promoCodeApp
  }
)
