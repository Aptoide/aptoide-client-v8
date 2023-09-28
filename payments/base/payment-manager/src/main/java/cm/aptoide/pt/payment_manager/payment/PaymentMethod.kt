package cm.aptoide.pt.payment_manager.payment

import cm.aptoide.pt.payment_manager.repository.product.domain.ProductInfoData
import cm.aptoide.pt.payment_manager.transaction.Transaction

interface PaymentMethod<T> {

  fun createTransaction(paymentDetails: T) : Transaction

  fun getProductInfo(): ProductInfoData

}
