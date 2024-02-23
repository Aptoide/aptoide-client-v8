package com.appcoins.payment_method.adyen.di

import com.appcoins.payment_method.adyen.repository.AdyenV2Repository
import com.appcoins.payment_method.adyen.repository.AdyenV2RepositoryImpl
import com.appcoins.payment_method.adyen.repository.AdyenV2RepositoryImpl.AdyenV2Api
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
  fun bindAdyenV2Repository(repository: AdyenV2RepositoryImpl): AdyenV2Repository

  companion object {
    @Singleton
    @Provides
    fun provideBrokerApi(
      @MicroServicesHostUrl baseUrl: String,
      @HighTimeoutOkHttp okHttpClient: OkHttpClient,
    ): AdyenV2Api = Retrofit.Builder()
      .baseUrl(baseUrl)
      .client(okHttpClient)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(AdyenV2Api::class.java)
  }
}
