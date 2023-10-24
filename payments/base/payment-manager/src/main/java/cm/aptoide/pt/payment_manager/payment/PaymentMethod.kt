package cm.aptoide.pt.payment_manager.payment

import cm.aptoide.pt.payment_manager.transaction.Transaction

interface PaymentMethod<T> {
  val id: String
  val label: String
  val iconUrl: String
  val available: Boolean

  fun createTransaction(paymentDetails: T): Transaction
}
