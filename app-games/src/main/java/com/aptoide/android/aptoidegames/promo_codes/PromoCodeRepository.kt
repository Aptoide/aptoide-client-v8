package com.aptoide.android.aptoidegames.promo_codes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromoCodeRepository @Inject constructor() {

  private val promoCode = MutableStateFlow<PromoCode?>(null)

  fun setPromoCode(pc: PromoCode?) {
    promoCode.tryEmit(pc)
  }

  fun promoCode(): Flow<PromoCode?> = promoCode
}
