package com.appcoins.payment_method.paypal.di

import com.appcoins.payment_method.paypal.repository.PaypalHttpHeadersProvider
import com.appcoins.payment_method.paypal.repository.PaypalHttpHeadersProviderImpl
import com.appcoins.payment_method.paypal.repository.PaypalRepository
import com.appcoins.payment_method.paypal.repository.PaypalRepositoryImpl
import com.appcoins.payment_method.paypal.repository.PaypalRepositoryImpl.PaypalV2Api
import com.appcoins.payments.network.di.HighTimeoutOkHttp
import com.appcoins.payments.network.di.MicroServicesHostUrl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface NetworkModule {

  @Singleton
  @Binds
  fun bindPaypalHttpHeadersProvider(provider: PaypalHttpHeadersProviderImpl): PaypalHttpHeadersProvider

  @Singleton
  @Binds
  fun bindPaypalRepository(repository: PaypalRepositoryImpl): PaypalRepository

  companion object {
    @Singleton
    @Provides
    fun providePaypalV2Api(
      @MicroServicesHostUrl baseUrl: String,
      @HighTimeoutOkHttp okHttpClient: OkHttpClient,
    ): PaypalV2Api = Retrofit.Builder()
      .baseUrl(baseUrl)
      .client(okHttpClient)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(PaypalV2Api::class.java)
  }
}
