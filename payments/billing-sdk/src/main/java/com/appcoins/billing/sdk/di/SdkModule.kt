package com.appcoins.billing.sdk.di

import com.appcoins.billing.AppcoinsBilling
import com.appcoins.billing.sdk.AppcoinsBillingBinder
import com.appcoins.billing.sdk.BillingErrorMapper
import com.appcoins.billing.sdk.BillingErrorMapperImpl
import com.appcoins.billing.sdk.sku_details.ProductSerializer
import com.appcoins.billing.sdk.sku_details.ProductSerializerImpl
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
  fun provideProductSerializer(productSerializer: ProductSerializerImpl): ProductSerializer

  @Singleton
  @Binds
  fun provideBillingSupportErrorMapper(billingSupportErrorMapper: BillingErrorMapperImpl): BillingErrorMapper

  @Singleton
  @Binds
  fun provideAppcoinsBillingStub(appcoinsBillingBinder: AppcoinsBillingBinder): AppcoinsBilling.Stub
}
