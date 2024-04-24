package com.appcoins.payments.arch

import com.appcoins.payments.json.Json

@Json
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
