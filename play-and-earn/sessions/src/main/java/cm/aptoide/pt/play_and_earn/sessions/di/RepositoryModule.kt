package cm.aptoide.pt.play_and_earn.sessions.di

import cm.aptoide.pt.aptoide_network.di.RawOkHttp
import cm.aptoide.pt.aptoide_network.di.RewardsDomain
import cm.aptoide.pt.environment_info.DeviceIdProvider
import cm.aptoide.pt.exception_handler.ExceptionHandler
import cm.aptoide.pt.play_and_earn.sessions.data.DefaultPaESessionsRepository
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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

  @Provides
  @Singleton
  fun providePaESessionsApi(
    @RawOkHttp okHttpClient: OkHttpClient,
    @RewardsDomain apiChainCatappultDomain: String,
    walletAuthInterceptor: WalletAuthInterceptor
  ): SessionsApi {
    val client = okHttpClient.newBuilder().addInterceptor(walletAuthInterceptor).build()

    return Retrofit.Builder()
      .client(client)
      .baseUrl(apiChainCatappultDomain)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(SessionsApi::class.java)
  }

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
