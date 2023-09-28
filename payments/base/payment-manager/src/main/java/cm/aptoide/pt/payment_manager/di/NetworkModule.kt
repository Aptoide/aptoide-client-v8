package cm.aptoide.pt.payment_manager.di

import cm.aptoide.pt.aptoide_network.di.BaseOkHttp
import cm.aptoide.pt.payment_manager.repository.broker.BrokerRepositoryImpl.BrokerApi
import cm.aptoide.pt.payment_manager.repository.product.ProductRepositoryImpl.ProductApi
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
  @RetrofitAPICatappult
  fun provideRetrofitAPIChain(
    @APICatappultUrl baseUrl: String,
    @BaseOkHttp okHttpClient: OkHttpClient,
  ): Retrofit =
    Retrofit.Builder()
      .baseUrl(baseUrl)
      .client(okHttpClient)
      .addConverterFactory(GsonConverterFactory.create())
      .build()

  @Singleton
  @Provides
  fun provideProductApi(@RetrofitAPICatappult retrofit: Retrofit): ProductApi =
    retrofit.create(ProductApi::class.java)

  @Singleton
  @Provides
  fun provideBrokerApi(@RetrofitAPICatappult retrofit: Retrofit): BrokerApi =
    retrofit.create(BrokerApi::class.java)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class APICatappultUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitAPICatappult
