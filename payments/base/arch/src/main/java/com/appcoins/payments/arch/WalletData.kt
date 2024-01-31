package com.appcoins.payments.arch

data class WalletData(
  val address: String,
  val ewt: String,
  val signature: String,
)
