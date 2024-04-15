package com.appcoins.payments.di

import android.content.Context
import com.appcoins.guest_wallet.BuildConfig
import com.appcoins.guest_wallet.RealWalletProvider
import com.appcoins.guest_wallet.repository.WalletRepositoryImpl
import com.appcoins.guest_wallet.unique_id.UniqueIDProviderImpl
import com.appcoins.guest_wallet.unique_id.generator.IDGeneratorImpl
import com.appcoins.guest_wallet.unique_id.repository.UniqueIdRepositoryImpl
import com.appcoins.payments.arch.WalletProvider

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
