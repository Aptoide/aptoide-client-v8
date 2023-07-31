package cm.aptoide.pt.appcoins.di

import cm.aptoide.pt.appcoins.repository.GamificationRepository.GamificationApi
import cm.aptoide.pt.aptoide_network.di.BaseOkHttp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

  @Singleton
  @Provides
  fun providesBonusAppcApi(
    @BaseOkHttp okHttpClient: OkHttpClient,
    @APIChainBDSDomain baseHost: String,
  ): GamificationApi = Retrofit.Builder()
    .client(okHttpClient)
    .baseUrl(baseHost)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(GamificationApi::class.java)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class APIChainBDSDomain
