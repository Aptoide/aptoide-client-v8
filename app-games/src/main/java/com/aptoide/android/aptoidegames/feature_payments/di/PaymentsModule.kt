package com.aptoide.android.aptoidegames.feature_payments.di

import com.appcoins.payments.uri_handler.PaymentScreenContentProvider
import com.aptoide.android.aptoidegames.feature_payments.AGPaymentScreenContentProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PaymentsModule {

  @Provides
  @Singleton
  fun provideScreenContentProvider(): PaymentScreenContentProvider =
    AGPaymentScreenContentProvider()
}
