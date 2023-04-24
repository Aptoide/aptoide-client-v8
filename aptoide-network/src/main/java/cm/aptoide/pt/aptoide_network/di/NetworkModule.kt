package cm.aptoide.pt.aptoide_network.di

import android.content.Context
import cm.aptoide.pt.aptoide_network.data.network.UserAgentInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  @BaseOkHttp
  @Provides
  @Singleton
  fun provideBaseOkHttpClient(
    @ApplicationContext context: Context,
    userAgentInterceptor: UserAgentInterceptor,
    @VersionCode versionCode: Int
  ): OkHttpClient {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BASIC
    val resources = context.resources
    val cache = Cache(context.cacheDir, 10 * 1024 * 1024)
    return OkHttpClient.Builder()
      .cache(cache)
      .addInterceptor(userAgentInterceptor)
      .addInterceptor(interceptor)
      .addInterceptor(Interceptor {
        val originalRequest = it.request()
        val newUrl = originalRequest.url.newBuilder()
          .addQueryParameter("aptoide_vercode", versionCode.toString())
          .addQueryParameter(
            "lang", resources.configuration.locale.language
                + "_"
                + resources.configuration.locale.country
          ).build()
        val newRequest = originalRequest.newBuilder().url(newUrl).build()
        it.proceed(newRequest)
      })
      .build()
  }

  @CampaignsOkHttp
  @Provides
  @Singleton
  fun provideCampaignsOkHttpClient(
    userAgentInterceptor: UserAgentInterceptor,
    @VersionCode versionCode: Int
  ): OkHttpClient {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BASIC
    return OkHttpClient.Builder()
      .addInterceptor(userAgentInterceptor)
      .addInterceptor(interceptor)
      .addInterceptor(Interceptor {
        val originalRequest = it.request()
        val newUrl = originalRequest.url.newBuilder()
          .addQueryParameter("aptoide_vercode", versionCode.toString())
          .build()
        val newRequest = originalRequest.newBuilder().url(newUrl).build()
        it.proceed(newRequest)
      })
      .build()
  }

  @RetrofitV7
  @Provides
  @Singleton
  fun provideRetrofitV7(
    @BaseOkHttp okHttpClient: OkHttpClient,
    @StoreDomain domain: String
  ): Retrofit {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(domain)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }

  @RetrofitV7ActionItem
  @Provides
  @Singleton
  fun provideRetrofitV7ActionItem(
    @BaseOkHttp okHttpClient: OkHttpClient,
    @StoreDomain domain: String,
  ): Retrofit {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(domain + "user/action/item/")
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }


  @RetrofitBuzz
  @Provides
  @Singleton
  fun provideSearchAutoCompleteRetrofit(@BaseOkHttp okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl("https://buzz.aptoide.com:10002")
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }

  @RetrofitAptWords
  @Provides
  @Singleton
  fun provideRetrofitAptWords(@BaseOkHttp okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl("https://webservices.aptwords.net/api/7/")
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }

  @RetrofitV8Echo
  @Provides
  @Singleton
  fun provideRetrofitV8Echo(@BaseOkHttp okHttpClient: OkHttpClient): Retrofit {
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

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class VersionCode

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CampaignsOkHttp


