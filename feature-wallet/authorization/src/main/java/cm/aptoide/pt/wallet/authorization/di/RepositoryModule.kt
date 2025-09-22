package cm.aptoide.pt.wallet.authorization.di

import android.content.Context
import cm.aptoide.pt.aptoide_network.di.RawOkHttp
import cm.aptoide.pt.aptoide_network.di.WebServicesDomain
import cm.aptoide.pt.wallet.authorization.data.UserAuthApi
import cm.aptoide.pt.wallet.authorization.data.WalletAuthRepository
import cm.aptoide.pt.wallet.authorization.data.WalletAuthRepositoryImpl
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

  @Singleton
  @Provides
  fun provideWalletAuthRepository(
    @ApplicationContext context: Context,
    userAuthApi: UserAuthApi,
    walletCoreDataSource: WalletCoreDataSource
  ): WalletAuthRepository {
    return WalletAuthRepositoryImpl(
      context = context,
      userAuthApi = userAuthApi,
      dispatcher = Dispatchers.IO,
      walletCoreDataSource = walletCoreDataSource
    )
  }
}
