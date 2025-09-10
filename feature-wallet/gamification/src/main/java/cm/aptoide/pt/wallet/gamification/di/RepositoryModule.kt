package cm.aptoide.pt.wallet.gamification.di

import cm.aptoide.pt.aptoide_network.di.ApiChainCatappultDomain
import cm.aptoide.pt.aptoide_network.di.BaseOkHttp
import cm.aptoide.pt.wallet.datastore.CurrencyPreferencesDataSource
import cm.aptoide.pt.wallet.gamification.data.DefaultGamificationRepository
import cm.aptoide.pt.wallet.gamification.data.GamificationApi
import cm.aptoide.pt.wallet.gamification.data.GamificationRepository
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
  fun provideGamificationApi(
    @BaseOkHttp okHttpClient: OkHttpClient,
    @ApiChainCatappultDomain apiChainCatappultDomain: String,
  ): GamificationApi {
    val gson = GsonBuilder()
      .setDateFormat("yyyy-MM-dd HH:mm")
      .create()
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(apiChainCatappultDomain)
      .addConverterFactory(GsonConverterFactory.create(gson))
      .build()
      .create(GamificationApi::class.java)
  }

  @Provides
  @Singleton
  fun provideGamificationRepository(
    gamificationApi: GamificationApi,
    currencyPreferencesDataSource: CurrencyPreferencesDataSource
  ): GamificationRepository = DefaultGamificationRepository(
    gamificationApi = gamificationApi,
    currencyPreferencesDataSource = currencyPreferencesDataSource,
    dispatcher = Dispatchers.IO
  )
}
