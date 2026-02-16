package cm.aptoide.pt.aptoide_network.di

import android.content.Context
import cm.aptoide.pt.aptoide_network.data.network.AABInterceptor
import cm.aptoide.pt.aptoide_network.data.network.AcceptLanguageInterceptor
import cm.aptoide.pt.aptoide_network.data.network.PostCacheInterceptor
import cm.aptoide.pt.aptoide_network.data.network.QLogicInterceptor
import cm.aptoide.pt.aptoide_network.data.network.QueryLangInterceptor
import cm.aptoide.pt.aptoide_network.data.network.UserAgentInterceptor
import cm.aptoide.pt.aptoide_network.data.network.VersionCodeInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BASIC
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit.SECONDS
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
    qLogicInterceptor: QLogicInterceptor,
    queryLangInterceptor: QueryLangInterceptor,
    versionCodeInterceptor: VersionCodeInterceptor,
    languageInterceptor: AcceptLanguageInterceptor,
    httpLoggingInterceptor: HttpLoggingInterceptor,
    postCacheInterceptor: PostCacheInterceptor,
    aabInterceptor: AABInterceptor
  ): OkHttpClient =
    OkHttpClient.Builder()
      .cache(Cache(context.cacheDir, 10 * 1024 * 1024))
      .addInterceptor(userAgentInterceptor)
      .addInterceptor(queryLangInterceptor)
      .addInterceptor(qLogicInterceptor)
      .addInterceptor(versionCodeInterceptor)
      .addInterceptor(languageInterceptor)
      .addInterceptor(httpLoggingInterceptor)
      .addInterceptor(postCacheInterceptor)
      .addInterceptor(aabInterceptor)
      .build()

  @SimpleOkHttp
  @Provides
  @Singleton
  fun provideCampaignsOkHttpClient(
    userAgentInterceptor: UserAgentInterceptor,
    versionCodeInterceptor: VersionCodeInterceptor,
    languageInterceptor: AcceptLanguageInterceptor,
    httpLoggingInterceptor: HttpLoggingInterceptor,
    postCacheInterceptor: PostCacheInterceptor
  ): OkHttpClient =
    OkHttpClient.Builder()
      .addInterceptor(userAgentInterceptor)
      .addInterceptor(httpLoggingInterceptor)
      .addInterceptor(versionCodeInterceptor)
      .addInterceptor(languageInterceptor)
      .addInterceptor(postCacheInterceptor)
      .build()

  @RawOkHttp
  @Provides
  @Singleton
  fun provideRTBOkHttpClient(
    userAgentInterceptor: UserAgentInterceptor,
    httpLoggingInterceptor: HttpLoggingInterceptor,
  ): OkHttpClient =
    OkHttpClient.Builder()
      .followRedirects(false)
      .followSslRedirects(false)
      .connectTimeout(10, SECONDS)
      .readTimeout(10, SECONDS)
      .addInterceptor(userAgentInterceptor)
      .addInterceptor(httpLoggingInterceptor)
      .build()

  @DownloadsOKHttp
  @Provides
  @Singleton
  fun provideDownloadsOkHttpClient(
    userAgentInterceptor: UserAgentInterceptor,
    versionCodeInterceptor: VersionCodeInterceptor,
    languageInterceptor: AcceptLanguageInterceptor,
    httpLoggingInterceptor: HttpLoggingInterceptor,
  ): OkHttpClient =
    OkHttpClient.Builder()
      .connectTimeout(20, SECONDS)
      .readTimeout(20, SECONDS)
      .writeTimeout(20, SECONDS)
      .addInterceptor(userAgentInterceptor)
      .addInterceptor(httpLoggingInterceptor)
      .addInterceptor(versionCodeInterceptor)
      .addInterceptor(languageInterceptor)
      .build()

  @GameGenieOkHttp
  @Provides
  @Singleton
  fun provideGameGenieOkHttpClient(
    @ApplicationContext context: Context,
    userAgentInterceptor: UserAgentInterceptor,
    qLogicInterceptor: QLogicInterceptor,
    queryLangInterceptor: QueryLangInterceptor,
    versionCodeInterceptor: VersionCodeInterceptor,
    languageInterceptor: AcceptLanguageInterceptor,
    httpLoggingInterceptor: HttpLoggingInterceptor,
    postCacheInterceptor: PostCacheInterceptor,
    aabInterceptor: AABInterceptor
  ): OkHttpClient =
    OkHttpClient.Builder()
      .cache(Cache(context.cacheDir, 10 * 1024 * 1024))
      .connectTimeout(30, SECONDS)
      .readTimeout(30, SECONDS)
      .writeTimeout(30, SECONDS)
      .addInterceptor(userAgentInterceptor)
      .addInterceptor(queryLangInterceptor)
      .addInterceptor(qLogicInterceptor)
      .addInterceptor(versionCodeInterceptor)
      .addInterceptor(languageInterceptor)
      .addInterceptor(httpLoggingInterceptor)
      .addInterceptor(postCacheInterceptor)
      .addInterceptor(aabInterceptor)
      .build()

  @RetrofitV7
  @Provides
  @Singleton
  fun provideRetrofitV7(
    @BaseOkHttp okHttpClient: OkHttpClient,
    @StoreDomain domain: String,
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

  @RetrofitCategoriesApps
  @Provides
  @Singleton
  fun provideRetrofitCategoriesApps(
    @BaseOkHttp okHttpClient: OkHttpClient,
    @StoreEnvironmentDomain domain: String,
  ): Retrofit {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(domain)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }

  @Provides
  @Singleton
  fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
    HttpLoggingInterceptor().apply { level = BASIC }
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
annotation class RetrofitCategoriesApps

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
annotation class StoreEnvironmentDomain

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HTMLGamesServiceKey

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class VersionCode

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SimpleOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RawOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DownloadsOKHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GameGenieOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SearchBuzzClientKey

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiChainCatappultDomain

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RewardsDomain

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WebServicesDomain
