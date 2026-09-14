package com.aptoide.android.aptoidegames.installer.gplay.di

import cm.aptoide.pt.aptoide_network.data.network.AcceptLanguageInterceptor
import cm.aptoide.pt.aptoide_network.data.network.UserAgentInterceptor
import cm.aptoide.pt.download_view.presentation.InlineInstallResolver
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.installer.PlayCatalogChecker
import com.aptoide.android.aptoidegames.installer.gplay.AptoideCatalogTokenRepository
import com.aptoide.android.aptoidegames.installer.gplay.CachingCatalogTokenRepository
import com.aptoide.android.aptoidegames.installer.gplay.CatalogTokenRepository
import com.aptoide.android.aptoidegames.installer.gplay.PlayInlineConfigApi
import com.aptoide.android.aptoidegames.installer.gplay.PlayInlineInstallResolver
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface InlineInstallModule {

  @Binds
  @Singleton
  fun bindInlineInstallResolver(impl: PlayInlineInstallResolver): InlineInstallResolver

  @Binds
  @Singleton
  fun bindCatalogTokenRepository(impl: CachingCatalogTokenRepository): CatalogTokenRepository

  @Binds
  @Singleton
  fun bindPlayCatalogChecker(impl: CachingCatalogTokenRepository): PlayCatalogChecker

  companion object {

    // Swap the origin for [FakeCatalogTokenRepository] to exercise the inline flow and the
    // "Google Play" labeling locally without the backend (every app becomes Play catalog)
    @Provides
    @Singleton
    fun provideCachingCatalogTokenRepository(
      origin: AptoideCatalogTokenRepository,
    ): CachingCatalogTokenRepository = CachingCatalogTokenRepository(
      origin = origin,
      scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
      now = System::currentTimeMillis,
    )

    @PlayInlineOkHttp
    @Provides
    @Singleton
    fun providePlayInlineOkHttpClient(
      userAgentInterceptor: UserAgentInterceptor,
      acceptLanguageInterceptor: AcceptLanguageInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
      .addInterceptor(userAgentInterceptor)
      .addInterceptor(
        HttpLoggingInterceptor().apply {
          level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
          } else {
            HttpLoggingInterceptor.Level.NONE
          }
        }
      )
      .addInterceptor(acceptLanguageInterceptor)
      .build()

    @Provides
    @Singleton
    fun providePlayInlineConfigApi(
      @PlayInlineOkHttp okHttpClient: OkHttpClient,
    ): PlayInlineConfigApi = Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(BuildConfig.APTOIDE_API_DOMAIN)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(PlayInlineConfigApi::class.java)
  }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PlayInlineOkHttp
