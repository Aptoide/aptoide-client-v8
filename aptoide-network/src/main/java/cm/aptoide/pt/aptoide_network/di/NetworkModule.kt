package cm.aptoide.pt.aptoide_network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  @Provides
  @Singleton
  fun provideOkHttpClient(): OkHttpClient {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BASIC
    return OkHttpClient.Builder()
      .addInterceptor(interceptor)
      .addNetworkInterceptor { chain ->
        chain.proceed(
          chain.request()
            .newBuilder()
            .header("User-Agent", "Aptoide/9.20.5.1 (Linux; Android 12; 32; Pixel 6 Build/oriole; aarch64; cm.aptoide.pt; 12002; c240e504e481e4b144c20654a752611d; 0x0; fff4758b-345e-46a7-8ab3-99ae63c6942d)")
            .build()
        )
      }
      .build()
  }


  @RetrofitV7
  @Provides
  @Singleton
  fun provideRetrofitV7(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl("https://ws75.aptoide.com/api/7/")
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }

  @RetrofitV7ActionItem
  @Provides
  @Singleton
  fun provideRetrofitV7ActionItem(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl("https://ws75.aptoide.com/api/7.20181019/user/action/item/")
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }


  @RetrofitBuzz
  @Provides
  @Singleton
  fun provideSearchAutoCompleteRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl("https://buzz.aptoide.com:10002")
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }

  @RetrofitV8Echo
  @Provides
  @Singleton
  fun provideRetrofitV8Echo(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl("https://api.aptoide.com/echo/8.20181122/")
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitBuzz

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitV7

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitV8Echo

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitV7ActionItem






