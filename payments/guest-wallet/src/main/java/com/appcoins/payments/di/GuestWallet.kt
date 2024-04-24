package com.appcoins.payments.di

import android.content.Context
import com.appcoins.payments.arch.WalletProvider
import com.appcoins.payments.guest_wallet.BuildConfig
import com.appcoins.payments.guest_wallet.RealWalletProvider
import com.appcoins.payments.guest_wallet.repository.WalletRepositoryImpl
import com.appcoins.payments.guest_wallet.unique_id.UniqueIDProviderImpl
import com.appcoins.payments.guest_wallet.unique_id.generator.IDGeneratorImpl
import com.appcoins.payments.guest_wallet.unique_id.repository.UniqueIdRepositoryImpl

val Payments.guestWalletProvider: WalletProvider by lazyInit {
  RealWalletProvider(
    uniqueIDProvider = UniqueIDProviderImpl(
      generator = IDGeneratorImpl(),
      uniqueIdRepository = UniqueIdRepositoryImpl(
        sharedPreferences = context.getSharedPreferences(
          "${BuildConfig.LIBRARY_PACKAGE_NAME}.unique_id_prefs",
          Context.MODE_PRIVATE
        )
      )
    ),
    walletRepository = WalletRepositoryImpl(backendRestClient)
  )
}
