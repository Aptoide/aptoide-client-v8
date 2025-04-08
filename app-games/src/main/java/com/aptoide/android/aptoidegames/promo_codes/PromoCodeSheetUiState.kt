package com.aptoide.android.aptoidegames.promo_codes

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_apps.data.walletApp

sealed class PromoCodeSheetUiState {
  data class Idle(val promoCodeApp: App, val walletApp: App) :
    PromoCodeSheetUiState()

  object Loading : PromoCodeSheetUiState()
  object NoConnection : PromoCodeSheetUiState()
  object Error : PromoCodeSheetUiState()
}

class PromoCodeSheetUiStateProvider : PreviewParameterProvider<PromoCodeSheetUiState> {
  override val values: Sequence<PromoCodeSheetUiState> = sequenceOf(
    PromoCodeSheetUiState.Idle(randomApp, walletApp),
    PromoCodeSheetUiState.Loading,
    PromoCodeSheetUiState.NoConnection,
    PromoCodeSheetUiState.Error
  )
}
