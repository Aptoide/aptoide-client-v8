package cm.aptoide.pt.guest_wallet.di

import cm.aptoide.pt.feature_payment.wallet.WalletProvider
import cm.aptoide.pt.guest_wallet.RealWalletProvider
import cm.aptoide.pt.guest_wallet.repository.wallet.WalletRepository
import cm.aptoide.pt.guest_wallet.repository.wallet.WalletRepositoryImpl
import cm.aptoide.pt.guest_wallet.unique_id.UniqueIDProvider
import cm.aptoide.pt.guest_wallet.unique_id.UniqueIDProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface PaymentModule {

  @Singleton
  @Binds
  fun bindUniqueIDProvider(uniqueIDProvider: UniqueIDProviderImpl): UniqueIDProvider

  @Singleton
  @Binds
  fun bindWalletRepository(walletRepository: WalletRepositoryImpl): WalletRepository

  @Singleton
  @Binds
  fun bindWalletProvider(walletProvider: RealWalletProvider): WalletProvider
}
