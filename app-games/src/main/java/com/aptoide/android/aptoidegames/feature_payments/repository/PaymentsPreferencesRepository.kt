package com.aptoide.android.aptoidegames.feature_payments.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.aptoide.android.aptoidegames.feature_payments.di.PaymentsPreferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentsPreferencesRepository @Inject constructor(
  @PaymentsPreferencesDataStore private val dataStore: DataStore<Preferences>,
) : PreSelectedPaymentMethodRepository {
  companion object PreferencesKeys {
    private val PRESELETED_PAYMENT_METHOD = stringPreferencesKey("preselected_payment_method")
  }

  override suspend fun setPreselectedPaymentMethod(id: String?) {
    dataStore.edit {
      if (id == null) {
        it.remove(PRESELETED_PAYMENT_METHOD)
      } else {
        it[PRESELETED_PAYMENT_METHOD] = id
      }
    }
  }

  override fun getPreselectedPaymentMethod(): Flow<String?> =
    dataStore.data.map { it[PRESELETED_PAYMENT_METHOD] }
}
