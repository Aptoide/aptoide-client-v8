package com.aptoide.pt.payments_prefs.data

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreSelectedPaymentStateRepository @Inject constructor(private val sharedPreferences: SharedPreferences) {

  companion object PreferencesKeys {
    val LAST_PAYMENT_METHOD_ID = "last_payment_method_id"
  }

  fun saveLastSuccessfulPaymentMethod(paymentMethodId: String) {
    val editor = sharedPreferences.edit()
    editor.putString(LAST_PAYMENT_METHOD_ID, paymentMethodId)
    editor.apply()
  }

  fun getLastSuccessfulPaymentMethod() =
    sharedPreferences.getString(LAST_PAYMENT_METHOD_ID, null)

  @Suppress("unused")
  fun hasLastSuccessfulPaymentMethod() =
    sharedPreferences.contains(LAST_PAYMENT_METHOD_ID)

  @Suppress("unused")
  fun removeLastSuccessfulPaymentMethod() {
    val editor = sharedPreferences.edit()
    editor.remove(LAST_PAYMENT_METHOD_ID)
    editor.apply()
  }
}
