package com.aptoide.android.aptoidegames.feature_payments.di

import android.content.Context
import com.appcoins.payments.uri_handler.PaymentScreenContentProvider
import com.aptoide.android.aptoidegames.feature_payments.AGPaymentScreenContentProvider
import com.aptoide.android.aptoidegames.feature_payments.repository.PaymentsPreferencesRepository
import com.aptoide.android.aptoidegames.feature_payments.repository.PreSelectedPaymentMethodRepository
import com.aptoide.android.aptoidegames.paymentsPreferencesDataStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface PaymentsModule {

  @Singleton
  @Binds
  fun bindsPreSelectedPaymentMethodRepository(paymentsPreferencesRepository: PaymentsPreferencesRepository): PreSelectedPaymentMethodRepository

  companion object {
    @Provides
    @Singleton
    fun provideScreenContentProvider(): PaymentScreenContentProvider =
      AGPaymentScreenContentProvider()

    @Provides
    @Singleton
    @PaymentsPreferencesDataStore
    fun providePaymentsPreferencesDataStore(@ApplicationContext appContext: Context) =
      appContext.paymentsPreferencesDataStore
  }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PaymentsPreferencesDataStore
