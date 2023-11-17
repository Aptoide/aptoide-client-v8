package com.aptoide.pt.payments_prefs.domain

import com.aptoide.pt.payments_prefs.data.PreSelectedPaymentStateRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreSelectedPaymentUseCase @Inject constructor(
  private val preSelectedPaymentStateRepository: PreSelectedPaymentStateRepository,
) {

  fun saveLastSuccessfulPaymentMethod(paymentMethodId: String) =
    preSelectedPaymentStateRepository.saveLastSuccessfulPaymentMethod(paymentMethodId)

  fun getLastSuccessfulPaymentMethod() = preSelectedPaymentStateRepository.getLastSuccessfulPaymentMethod()
}
