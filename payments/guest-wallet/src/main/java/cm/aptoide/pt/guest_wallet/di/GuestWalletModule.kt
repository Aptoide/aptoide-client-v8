package cm.aptoide.pt.guest_wallet.di

import android.content.Context
import android.content.SharedPreferences
import cm.aptoide.pt.guest_wallet.BuildConfig
import cm.aptoide.pt.guest_wallet.RealWalletProvider
import cm.aptoide.pt.guest_wallet.repository.wallet.WalletRepository
import cm.aptoide.pt.guest_wallet.repository.wallet.WalletRepositoryImpl
import cm.aptoide.pt.guest_wallet.unique_id.UniqueIDProvider
import cm.aptoide.pt.guest_wallet.unique_id.UniqueIDProviderImpl
import cm.aptoide.pt.guest_wallet.unique_id.generator.IDGenerator
import cm.aptoide.pt.guest_wallet.unique_id.generator.IDGeneratorImpl
import cm.aptoide.pt.guest_wallet.unique_id.repository.UniqueIdRepository
import cm.aptoide.pt.guest_wallet.unique_id.repository.UniqueIdRepositoryImpl
import cm.aptoide.pt.payment_manager.wallet.WalletProvider
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
  fun bindWalletRepository(walletRepository: WalletRepositoryImpl): WalletRepository

  @Singleton
  @Binds
  fun bindWalletProvider(walletProvider: RealWalletProvider): WalletProvider

  companion object {

    @Singleton
    @Provides
    @JvmStatic
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
