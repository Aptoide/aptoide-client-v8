package com.aptoide.android.aptoidegames.promo_codes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_apps.data.walletApp
import cm.aptoide.pt.feature_apps.domain.AppMetaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

class PromoCodeSheetViewModel(
  private val promoCode: PromoCode,
  private val appMetaUseCase: AppMetaUseCase,
) : ViewModel() {

  private val viewModelState =
    MutableStateFlow<PromoCodeSheetUiState>(PromoCodeSheetUiState.Loading)

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
      viewModelState.update { PromoCodeSheetUiState.Loading }

      try {
        val promoCodeApp = appMetaUseCase.getMetaInfo(promoCode.asSource())
        val walletApp = appMetaUseCase.getMetaInfo(walletApp.asSource())
        viewModelState.update {
          PromoCodeSheetUiState.Idle(promoCodeApp = promoCodeApp, walletApp = walletApp)
        }
      } catch (e: Throwable) {
        Timber.e(e)
        viewModelState.update {
          when (e) {
            is IOException -> PromoCodeSheetUiState.NoConnection
            else -> PromoCodeSheetUiState.Error
          }
        }
      }
    }
  }
}
