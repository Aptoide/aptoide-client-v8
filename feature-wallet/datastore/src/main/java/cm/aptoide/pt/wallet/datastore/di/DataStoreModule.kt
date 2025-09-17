package cm.aptoide.pt.wallet.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

val Context.currencyPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
  name = "currencyPreferencesDataStore"
)

val Context.walletCoreDataStore: DataStore<Preferences> by preferencesDataStore(
  name = "walletCoreDataStore"
)

@Module
@InstallIn(SingletonComponent::class)
internal object DataStoreModule {

  @Singleton
  @Provides
  @CurrencyPreferencesDataStore
  fun provideCurrencyPreferencesDataStore(
    @ApplicationContext appContext: Context,
  ): DataStore<Preferences> {
    return appContext.currencyPreferencesDataStore
  }

  @Singleton
  @Provides
  @WalletCoreDataStore
  fun provideWalletCoreDataStore(
    @ApplicationContext appContext: Context,
  ): DataStore<Preferences> {
    return appContext.walletCoreDataStore
  }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CurrencyPreferencesDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WalletCoreDataStore
