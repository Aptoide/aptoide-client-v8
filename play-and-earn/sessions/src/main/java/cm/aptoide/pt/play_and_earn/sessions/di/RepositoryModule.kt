package cm.aptoide.pt.play_and_earn.sessions.di

import cm.aptoide.pt.aptoide_network.data.network.PlayAndEarnIdInterceptor
import cm.aptoide.pt.aptoide_network.di.RawOkHttp
import cm.aptoide.pt.aptoide_network.di.RewardsDomain
import cm.aptoide.pt.environment_info.DeviceIdProvider
import cm.aptoide.pt.exception_handler.ExceptionHandler
import cm.aptoide.pt.play_and_earn.sessions.data.AccountStatusApi
import cm.aptoide.pt.play_and_earn.sessions.data.DefaultPaEAccountStatusRepository
import cm.aptoide.pt.play_and_earn.sessions.data.DefaultPaESessionsRepository
import cm.aptoide.pt.play_and_earn.sessions.data.PaEAccountStatusRepository
import cm.aptoide.pt.play_and_earn.sessions.data.PaESessionsRepository
import cm.aptoide.pt.play_and_earn.sessions.data.SessionsApi
import cm.aptoide.pt.wallet.authorization.data.WalletAuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class PaESessionsRetrofit

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

  // The sessions and the account status (AND-951) share one authenticated client: the same
  // rewards host, the same wallet JWT and Play & Earn id.
  @Provides
  @Singleton
  @PaESessionsRetrofit
  fun providePaESessionsRetrofit(
    @RawOkHttp okHttpClient: OkHttpClient,
    @RewardsDomain apiChainCatappultDomain: String,
    walletAuthInterceptor: WalletAuthInterceptor,
    playAndEarnIdInterceptor: PlayAndEarnIdInterceptor
  ): Retrofit {
    val client = okHttpClient.newBuilder()
      .addInterceptor(walletAuthInterceptor)
      .addInterceptor(playAndEarnIdInterceptor)
      .build()

    return Retrofit.Builder()
      .client(client)
      .baseUrl(apiChainCatappultDomain)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }

  @Provides
  @Singleton
  fun providePaESessionsApi(@PaESessionsRetrofit retrofit: Retrofit): SessionsApi =
    retrofit.create(SessionsApi::class.java)

  @Provides
  @Singleton
  fun provideAccountStatusApi(@PaESessionsRetrofit retrofit: Retrofit): AccountStatusApi =
    retrofit.create(AccountStatusApi::class.java)

  @Provides
  @Singleton
  fun providePaEAccountStatusRepository(
    accountStatusApi: AccountStatusApi,
  ): PaEAccountStatusRepository = DefaultPaEAccountStatusRepository(
    api = accountStatusApi,
    dispatcher = Dispatchers.IO,
  )

  @Provides
  @Singleton
  fun providePaESessionsRepository(
    sessionsApi: SessionsApi,
    deviceIdProvider: DeviceIdProvider,
    exceptionHandler: ExceptionHandler
  ): PaESessionsRepository = DefaultPaESessionsRepository(
    sessionsApi = sessionsApi,
    deviceIdProvider = deviceIdProvider,
    dispatcher = Dispatchers.IO,
    exceptionHandler = exceptionHandler
  )
}
