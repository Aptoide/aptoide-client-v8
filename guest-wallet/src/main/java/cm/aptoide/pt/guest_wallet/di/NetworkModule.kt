package cm.aptoide.pt.guest_wallet.di

import cm.aptoide.pt.aptoide_network.di.BaseOkHttp
import cm.aptoide.pt.guest_wallet.repository.wallet.WalletRepositoryImpl.WalletApi
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
internal class NetworkModule {

  @Singleton
  @Provides
  @RetrofitAPIChainCatappult
  fun provideRetrofitAPICatappult(
    @APIChainCatappultUrl baseUrl: String,
    @BaseOkHttp okHttpClient: OkHttpClient,
  ): Retrofit =
    Retrofit.Builder()
      .baseUrl(baseUrl)
      .client(okHttpClient)
      .addConverterFactory(GsonConverterFactory.create())
      .build()

  @Provides
  fun provideWalletApi(@RetrofitAPIChainCatappult retrofit: Retrofit): WalletApi =
    retrofit.create(WalletApi::class.java)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class APIChainCatappultUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitAPIChainCatappult
