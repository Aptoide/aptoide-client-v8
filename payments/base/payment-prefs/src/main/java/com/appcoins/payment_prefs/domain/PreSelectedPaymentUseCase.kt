package com.appcoins.payment_prefs.domain

import com.appcoins.payment_prefs.data.PreSelectedPaymentStateRepository

class PreSelectedPaymentUseCase internal constructor(
  private val preSelectedPaymentStateRepository: PreSelectedPaymentStateRepository,
) {

  fun saveLastSuccessfulPaymentMethod(paymentMethodId: String) =
    preSelectedPaymentStateRepository.saveLastSuccessfulPaymentMethod(paymentMethodId)

  fun getLastSuccessfulPaymentMethod() =
    preSelectedPaymentStateRepository.getLastSuccessfulPaymentMethod()
}
