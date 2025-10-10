package cm.aptoide.pt.wallet.authorization.di

import android.content.Context
import cm.aptoide.pt.aptoide_network.di.ApiChainCatappultDomain
import cm.aptoide.pt.aptoide_network.di.RawOkHttp
import cm.aptoide.pt.aptoide_network.di.WebServicesDomain
import cm.aptoide.pt.wallet.authorization.data.UserAuthApi
import cm.aptoide.pt.wallet.authorization.data.UserWalletAuthDataStore
import cm.aptoide.pt.wallet.authorization.data.WalletAuthRepository
import cm.aptoide.pt.wallet.authorization.data.WalletAuthRepositoryImpl
import cm.aptoide.pt.wallet.authorization.data.WalletRefreshApi
import cm.aptoide.pt.wallet.datastore.WalletCoreDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class RepositoryModule {

  @Provides
  @Singleton
  fun provideUserAuthApi(
    @RawOkHttp okHttpClient: OkHttpClient,
    @WebServicesDomain domain: String,
  ): UserAuthApi {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(domain)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(UserAuthApi::class.java)
  }

  @Provides
  @Singleton
  fun provideWalletRefreshApi(
    @RawOkHttp okHttpClient: OkHttpClient,
    @ApiChainCatappultDomain domain: String,
  ): WalletRefreshApi {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(domain)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(WalletRefreshApi::class.java)
  }

  @Singleton
  @Provides
  fun provideWalletAuthRepository(
    @ApplicationContext context: Context,
    userAuthApi: UserAuthApi,
    walletRefreshApi: WalletRefreshApi,
    walletCoreDataSource: WalletCoreDataSource,
    userWalletAuthDataStore: UserWalletAuthDataStore
  ): WalletAuthRepository {
    return WalletAuthRepositoryImpl(
      context = context,
      userAuthApi = userAuthApi,
      walletRefreshApi = walletRefreshApi,
      dispatcher = Dispatchers.IO,
      walletCoreDataSource = walletCoreDataSource,
      userWalletAuthDataStore = userWalletAuthDataStore
    )
  }
}
