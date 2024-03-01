package com.appcoins.payment_prefs.di

import android.content.Context
import com.appcoins.payment_prefs.BuildConfig
import com.appcoins.payment_prefs.data.PreSelectedPaymentStateRepository
import com.appcoins.payment_prefs.domain.PreSelectedPaymentUseCase
import com.appcoins.payments.arch.PaymentsInitializer

object PaymentPrefsModule {

  val preSelectedPaymentUseCase by lazy {
    val sharedPreferences = PaymentsInitializer.context.getSharedPreferences(
      "${BuildConfig.LIBRARY_PACKAGE_NAME}.payments",
      Context.MODE_PRIVATE
    )

    PreSelectedPaymentUseCase(
      PreSelectedPaymentStateRepository(sharedPreferences)
    )
  }
}
