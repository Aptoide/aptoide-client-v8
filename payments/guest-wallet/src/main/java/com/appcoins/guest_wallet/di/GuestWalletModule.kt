package com.appcoins.guest_wallet.di

import android.content.Context
import com.appcoins.guest_wallet.BuildConfig
import com.appcoins.guest_wallet.RealWalletProvider
import com.appcoins.guest_wallet.repository.WalletRepositoryImpl
import com.appcoins.guest_wallet.unique_id.UniqueIDProviderImpl
import com.appcoins.guest_wallet.unique_id.generator.IDGeneratorImpl
import com.appcoins.guest_wallet.unique_id.repository.UniqueIdRepositoryImpl
import com.appcoins.payments.arch.PaymentsInitializer
import com.appcoins.payments.arch.WalletProvider
import com.appcoins.payments.network.di.NetworkModule

object GuestWalletModule {
  fun getWalletProvider(): WalletProvider = RealWalletProvider(
    uniqueIDProvider = UniqueIDProviderImpl(
      generator = IDGeneratorImpl(),
      uniqueIdRepository = UniqueIdRepositoryImpl(
        sharedPreferences = PaymentsInitializer.context.getSharedPreferences(
          "${BuildConfig.LIBRARY_PACKAGE_NAME}.unique_id_prefs",
          Context.MODE_PRIVATE
        )
      )
    ),
    walletRepository = WalletRepositoryImpl(NetworkModule.backendRestClient)
  )
}
