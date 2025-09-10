package cm.aptoide.pt.appcoins.domain

import java.math.BigDecimal

data class WalletInfo(
  val wallet: String,
  val walletBalance: FiatValue,
  val unitsBalance: Long,
  val blocked: Boolean,
  val verified: Boolean,
)

data class FiatValue(val amount: BigDecimal, val currency: String, val symbol: String = "")
