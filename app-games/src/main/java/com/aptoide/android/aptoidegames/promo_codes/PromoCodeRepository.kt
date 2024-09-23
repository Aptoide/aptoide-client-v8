package com.aptoide.android.aptoidegames.promo_codes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromoCodeRepository @Inject constructor() {

  private val promoCodeApp = MutableStateFlow<PromoCodeApp?>(null)

  fun setPromoCodeApp(pcApp: PromoCodeApp?) {
    promoCodeApp.tryEmit(pcApp)
  }

  fun promoCodeApp(): Flow<PromoCodeApp?> = promoCodeApp
}
