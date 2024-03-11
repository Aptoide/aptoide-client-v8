package com.appcoins.guest_wallet.di

import android.content.Context
import android.content.SharedPreferences
import com.appcoins.guest_wallet.BuildConfig
import com.appcoins.guest_wallet.RealWalletProvider
import com.appcoins.guest_wallet.repository.WalletRepository
import com.appcoins.guest_wallet.repository.WalletRepositoryImpl
import com.appcoins.guest_wallet.unique_id.UniqueIDProvider
import com.appcoins.guest_wallet.unique_id.UniqueIDProviderImpl
import com.appcoins.guest_wallet.unique_id.generator.IDGenerator
import com.appcoins.guest_wallet.unique_id.generator.IDGeneratorImpl
import com.appcoins.guest_wallet.unique_id.repository.UniqueIdRepository
import com.appcoins.guest_wallet.unique_id.repository.UniqueIdRepositoryImpl
import com.appcoins.payments.arch.WalletProvider
import com.appcoins.payments.arch.GetUserAgent
import com.appcoins.payments.network.RestClient
import com.appcoins.payments.network.di.BackendHostUrl
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
internal interface GuestWalletModule {

  @Singleton
  @Binds
  fun bindIDGenerator(idGenerator: IDGeneratorImpl): IDGenerator

  @Singleton
  @Binds
  fun bindUniqueIDProvider(uniqueIDProvider: UniqueIDProviderImpl): UniqueIDProvider

  @Singleton
  @Binds
  fun bindUniqueIdRepositoryImpl(uniqueIdRepositoryImpl: UniqueIdRepositoryImpl): UniqueIdRepository

  @Singleton
  @Binds
  fun bindWalletProvider(walletProvider: RealWalletProvider): WalletProvider

  companion object {

    @Singleton
    @Provides
    fun provideWalletRepository(
      @BackendHostUrl baseUrl: String,
      getUserAgent: GetUserAgent,
    ): WalletRepository = WalletRepositoryImpl(
      RestClient.with(
        baseUrl = baseUrl,
        getUserAgent = getUserAgent
      )
    )

    @Singleton
    @Provides
    @UniqueIdSharedPreferences
    fun provideUniqueIdSharedPrefences(@ApplicationContext context: Context): SharedPreferences =
      context.getSharedPreferences(
        "${BuildConfig.LIBRARY_PACKAGE_NAME}.unique_id_prefs",
        Context.MODE_PRIVATE
      )
  }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UniqueIdSharedPreferences
