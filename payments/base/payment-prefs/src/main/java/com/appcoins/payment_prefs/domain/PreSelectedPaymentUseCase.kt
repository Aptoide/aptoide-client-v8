package com.appcoins.payment_prefs.domain

import com.appcoins.payment_prefs.data.PreSelectedPaymentStateRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreSelectedPaymentUseCase @Inject constructor(
  private val preSelectedPaymentStateRepository: PreSelectedPaymentStateRepository,
) {

  fun saveLastSuccessfulPaymentMethod(paymentMethodId: String) =
    preSelectedPaymentStateRepository.saveLastSuccessfulPaymentMethod(paymentMethodId)

  fun getLastSuccessfulPaymentMethod() = null//preSelectedPaymentStateRepository.getLastSuccessfulPaymentMethod()
}
