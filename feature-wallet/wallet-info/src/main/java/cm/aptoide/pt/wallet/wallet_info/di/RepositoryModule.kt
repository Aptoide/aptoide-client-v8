package cm.aptoide.pt.wallet.wallet_info.di

import cm.aptoide.pt.aptoide_network.di.ApiChainCatappultDomain
import cm.aptoide.pt.aptoide_network.di.BaseOkHttp
import cm.aptoide.pt.wallet.datastore.CurrencyPreferencesDataSource
import cm.aptoide.pt.wallet.wallet_info.data.DefaultWalletInfoRepository
import cm.aptoide.pt.wallet.wallet_info.data.WalletInfoApi
import cm.aptoide.pt.wallet.wallet_info.data.WalletInfoRepository
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

  @Provides
  @Singleton
  fun provideWalletInfoApi(
    @BaseOkHttp okHttpClient: OkHttpClient,
    @ApiChainCatappultDomain apiChainCatappultDomain: String,
  ): WalletInfoApi {
    val gson = GsonBuilder()
      .setDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
      .create()
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(apiChainCatappultDomain)
      .addConverterFactory(GsonConverterFactory.create(gson))
      .build()
      .create(WalletInfoApi::class.java)
  }

  @Provides
  @Singleton
  fun provideWalletInfoRepository(
    walletInfoApi: WalletInfoApi,
    currencyPreferencesDataSource: CurrencyPreferencesDataSource
  ): WalletInfoRepository = DefaultWalletInfoRepository(
    walletInfoApi = walletInfoApi,
    currencyPreferencesDataSource = currencyPreferencesDataSource,
    dispatcher = Dispatchers.IO
  )
}
