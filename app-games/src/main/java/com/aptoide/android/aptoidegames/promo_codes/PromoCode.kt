package com.aptoide.android.aptoidegames.promo_codes

import cm.aptoide.pt.extensions.getRandomString
import cm.aptoide.pt.feature_apps.domain.AppSource
import com.google.errorprone.annotations.Keep
import kotlin.random.Random

@Keep
data class PromoCode(
  override val packageName: String,
  val code: String,
) : AppSource

val randomPromoCode = PromoCode(
  packageName = getRandomString(range = 3..5, separator = "."),
  code = Random.nextLong().toString()
)
