package com.aptoide.android.aptoidegames.promo_codes

import cm.aptoide.pt.feature_apps.domain.AppSource
import com.google.errorprone.annotations.Keep

@Keep
data class PromoCodeApp(
  override val packageName: String,
  val promoCode: String,
) : AppSource
