package com.aptoide.android.aptoidegames.promo_codes

import cm.aptoide.pt.extensions.getRandomString
import cm.aptoide.pt.feature_apps.domain.AppSource
import com.google.errorprone.annotations.Keep
import kotlin.random.Random

@Keep
data class PromoCodeApp(
  override val packageName: String,
  val promoCode: String,
) : AppSource

val randomPromoCodeApp = PromoCodeApp(
  packageName = getRandomString(range = 3..5, separator = "."),
  promoCode = Random.nextLong().toString()
)
