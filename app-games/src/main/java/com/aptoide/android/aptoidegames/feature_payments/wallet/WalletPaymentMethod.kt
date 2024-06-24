package com.aptoide.android.aptoidegames.feature_payments.wallet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.appcoins.payments.arch.PaymentMethod
import com.appcoins.payments.arch.ProductInfoData
import com.appcoins.payments.arch.PurchaseRequest
import com.appcoins.payments.arch.Transaction
import com.appcoins.payments.arch.WalletData
import com.aptoide.android.aptoidegames.feature_payments.currentProductInfo

class WalletPaymentMethod(
  override val productInfo: ProductInfoData,
  override val purchaseRequest: PurchaseRequest,
) : PaymentMethod<Unit> {
  override val id: String = "wallet"
  override val label: String = "Wallet"
  override val iconUrl: String =
    "https://cdn6.aptoide.com/imgs/d/7/f/d7fef78e286470c19e2f49bad6102d5b_icon.png?w=128"
  override val available: Boolean = true
  override val wallet: WalletData
    get() = TODO("Not yet implemented")

  override suspend fun createTransaction(paymentDetails: Unit): Transaction {
    TODO("Not yet implemented")
  }
}

@Composable
fun rememberWalletPaymentMethod(purchaseRequest: PurchaseRequest): WalletPaymentMethod? {
  val productInfo = currentProductInfo()
  val paymentMethod by remember(key1 = purchaseRequest, key2 = productInfo) {
    derivedStateOf {
      productInfo ?: return@derivedStateOf null
      WalletPaymentMethod(productInfo, purchaseRequest)
    }
  }
  return paymentMethod
}
