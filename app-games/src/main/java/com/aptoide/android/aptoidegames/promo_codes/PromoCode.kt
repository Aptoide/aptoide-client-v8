package com.aptoide.android.aptoidegames.promo_codes

import cm.aptoide.pt.extensions.getRandomString
import cm.aptoide.pt.feature_apps.domain.AppSource
import com.google.errorprone.annotations.Keep
import kotlin.random.Random
import kotlin.random.nextInt

@Keep
data class PromoCode(
  override val packageName: String,
  val code: String,
  val value: Int? = null,
) : AppSource

val randomPromoCode = PromoCode(
  packageName = getRandomString(range = 3..5, separator = "."),
  code = Random.nextLong().toString(),
  value = Random.nextInt(5..80)
)
