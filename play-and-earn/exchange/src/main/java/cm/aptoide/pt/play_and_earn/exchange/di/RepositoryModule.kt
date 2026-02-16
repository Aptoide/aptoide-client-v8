package cm.aptoide.pt.play_and_earn.exchange.di

import cm.aptoide.pt.aptoide_network.di.RewardsDomain
import cm.aptoide.pt.play_and_earn.exchange.data.DefaultExchangeRepository
import cm.aptoide.pt.play_and_earn.exchange.data.ExchangeApi
import cm.aptoide.pt.play_and_earn.exchange.data.ExchangeRepository
import cm.aptoide.pt.play_and_earn.exchange.data.model.ExchangeDetailDeserializer
import cm.aptoide.pt.play_and_earn.exchange.data.model.ExchangeDetailJson
import cm.aptoide.pt.wallet.authorization.data.WalletAuthInterceptor
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

  @Provides
  @Singleton
  @ExchangeOkHttp
  fun provideExchangeOkHttpClient(
    walletAuthInterceptor: WalletAuthInterceptor,
    httpLoggingInterceptor: HttpLoggingInterceptor
  ): OkHttpClient {
    return OkHttpClient.Builder()
      .addInterceptor(walletAuthInterceptor)
      .addInterceptor(httpLoggingInterceptor)
      .build()
  }

  @Provides
  @Singleton
  fun provideExchangeApi(
    @ExchangeOkHttp okHttpClient: OkHttpClient,
    @RewardsDomain rewardsDomain: String,
  ): ExchangeApi {
    val gson = GsonBuilder()
      .registerTypeAdapter(ExchangeDetailJson::class.java, ExchangeDetailDeserializer())
      .create()


    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(rewardsDomain)
      .addConverterFactory(GsonConverterFactory.create(gson))
      .build()
      .create(ExchangeApi::class.java)
  }

  @Provides
  @Singleton
  fun provideExchangeRepository(
    exchangeApi: ExchangeApi
  ): ExchangeRepository = DefaultExchangeRepository(
    exchangeApi = exchangeApi,
    dispatcher = Dispatchers.IO
  )
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ExchangeOkHttp
