package cm.aptoide.pt.payment_manager.payment

import cm.aptoide.pt.payment_manager.transaction.Transaction

interface PaymentMethod<T> {

  fun createTransaction(paymentDetails: T) : Transaction
}
