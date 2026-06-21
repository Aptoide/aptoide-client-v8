package cm.aptoide.pt.play_and_earn.events.di

import cm.aptoide.pt.aptoide_network.di.RewardsDomain
import cm.aptoide.pt.play_and_earn.events.data.DefaultEventsRepository
import cm.aptoide.pt.play_and_earn.events.data.EventsApi
import cm.aptoide.pt.play_and_earn.events.data.EventsRepository
import cm.aptoide.pt.wallet.authorization.data.WalletAuthInterceptor
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
  @EventsOkHttp
  fun provideEventsOkHttpClient(
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
  fun provideEventsApi(
    @EventsOkHttp okHttpClient: OkHttpClient,
    @RewardsDomain rewardsDomain: String,
  ): EventsApi {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(rewardsDomain)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(EventsApi::class.java)
  }

  @Provides
  @Singleton
  fun provideEventsRepository(
    eventsApi: EventsApi
  ): EventsRepository = DefaultEventsRepository(
    eventsApi = eventsApi,
    dispatcher = Dispatchers.IO
  )
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EventsOkHttp
