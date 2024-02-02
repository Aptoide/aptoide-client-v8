package com.appcoins.payments.arch

data class WalletData(
  val address: String,
  val ewt: String,
  val signature: String,
)

val emptyWalletData = WalletData(
  address = "wallet address",
  ewt = "wallet ewt",
  signature = "wallet signature"
)
