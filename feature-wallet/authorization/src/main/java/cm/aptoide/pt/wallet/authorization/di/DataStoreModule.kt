package cm.aptoide.pt.wallet.authorization.di

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

val Context.walletAuthDataStore: DataStore<Preferences> by preferencesDataStore(
  name = "walletAuthDataStore"
)

@Module
@InstallIn(SingletonComponent::class)
internal object DataStoreModule {

  @Singleton
  @Provides
  @WalletAuthDataStore
  fun provideWalletAuthDataStore(
    @ApplicationContext appContext: Context,
  ): DataStore<Preferences> {
    return appContext.walletAuthDataStore
  }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WalletAuthDataStore