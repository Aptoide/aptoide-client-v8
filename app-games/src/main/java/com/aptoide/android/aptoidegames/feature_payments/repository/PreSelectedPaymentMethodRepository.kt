package com.aptoide.android.aptoidegames.feature_payments.repository

import kotlinx.coroutines.flow.Flow

interface PreSelectedPaymentMethodRepository {
  suspend fun setPreselectedPaymentMethod(id: String?)
  fun getPreselectedPaymentMethod(): Flow<String?>
}
