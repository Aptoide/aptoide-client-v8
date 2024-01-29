package com.appcoins.billing.sdk

internal enum class BillingType(val type: String) {
  INAPP("inapp"),
  SUBSCRIPTION("subs")
}

internal fun String.toBillingType() =
  BillingType.values().firstOrNull { it.type == this }
