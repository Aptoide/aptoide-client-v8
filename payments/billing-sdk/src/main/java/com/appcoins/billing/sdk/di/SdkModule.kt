package com.appcoins.billing.sdk.di

import com.appcoins.billing.AppcoinsBilling
import com.appcoins.billing.sdk.AppcoinsBillingBinder
import com.appcoins.billing.sdk.billing_support.BillingSupportErrorMapper
import com.appcoins.billing.sdk.billing_support.BillingSupportErrorMapperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface SdkModule {

  @Singleton
  @Binds
  fun provideBillingSupportErrorMapper(billingSupportErrorMapper: BillingSupportErrorMapperImpl): BillingSupportErrorMapper

  @Singleton
  @Binds
  fun provideAppcoinsBillingStub(appcoinsBillingBinder: AppcoinsBillingBinder): AppcoinsBilling.Stub
}
