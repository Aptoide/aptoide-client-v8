package cm.aptoide.pt.aptoide_network.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
  fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BASIC
    val resources = context.resources
    return OkHttpClient.Builder()
      .addInterceptor(interceptor)
      .addNetworkInterceptor { chain ->
        chain.proceed(
          chain.request()
            .newBuilder()
            .header(
              "lang",
              resources.configuration.locale.language
                  + "_"
                  + resources.configuration.locale.country
            )
            .build()
        )
      }
      .build()
  }

  @RetrofitV7
  @Provides
  @Singleton
  fun provideRetrofitV7(okHttpClient: OkHttpClient, @StoreDomain domain: String): Retrofit {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(domain)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }

  @RetrofitV7AppsGroup
  @Provides
  @Singleton
  fun provideRetrofitV7AppsGroup(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl("https://ws75.aptoide.com/api/7.20221201/")
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

  @RetrofitAptWords
  @Provides
  @Singleton
  fun provideRetrofitAptWords(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl("https://webservices.aptwords.net/api/7/")
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
annotation class RetrofitV7AppsGroup

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitAptWords

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitV8Echo

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitV7ActionItem

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StoreName

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StoreDomain




