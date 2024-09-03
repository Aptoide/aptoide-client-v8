package com.aptoide.android.aptoidegames.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import cm.aptoide.pt.aptoide_network.data.network.GetAcceptLanguage
import cm.aptoide.pt.aptoide_network.data.network.GetUserAgent
import cm.aptoide.pt.aptoide_network.data.network.QLogicInterceptor
import cm.aptoide.pt.aptoide_network.di.BaseOkHttp
import cm.aptoide.pt.aptoide_network.di.RetrofitBuzz
import cm.aptoide.pt.aptoide_network.di.StoreDomain
import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.aptoide_network.di.VersionCode
import cm.aptoide.pt.environment_info.DeviceInfo
import cm.aptoide.pt.feature_editorial.di.DefaultEditorialUrl
import cm.aptoide.pt.feature_flags.data.FeatureFlagsRepository
import cm.aptoide.pt.feature_flags.di.FeatureFlagsDataStore
import cm.aptoide.pt.feature_home.di.WidgetsUrl
import cm.aptoide.pt.feature_mmp.di.MMPDomain
import cm.aptoide.pt.feature_oos.di.UninstallPackagesFilter
import cm.aptoide.pt.feature_search.data.AutoCompleteSuggestionsRepository
import cm.aptoide.pt.feature_search.domain.repository.SearchStoreManager
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.appLaunchDataStore
import com.aptoide.android.aptoidegames.dataStore
import com.aptoide.android.aptoidegames.feature_flags.AptoideFeatureFlagsRepository
import com.aptoide.android.aptoidegames.home.repository.ThemePreferencesManager
import com.aptoide.android.aptoidegames.launch.AppLaunchPreferencesManager
import com.aptoide.android.aptoidegames.network.AptoideGetHeaders
import com.aptoide.android.aptoidegames.network.AptoideQLogicInterceptor
import com.aptoide.android.aptoidegames.networkPreferencesDataStore
import com.aptoide.android.aptoidegames.permissions.AppPermissionsManager
import com.aptoide.android.aptoidegames.search.repository.AppGamesAutoCompleteSuggestionsRepository
import com.aptoide.android.aptoidegames.search.repository.AppGamesAutoCompleteSuggestionsRepository.AutoCompleteSearchRetrofitService
import com.aptoide.android.aptoidegames.search.repository.AppGamesSearchStoreManager
import com.aptoide.android.aptoidegames.themeDataStore
import com.aptoide.android.aptoidegames.userFeatureFlagsDataStore
import com.aptoide.android.aptoidegames.wallet.WALLET_PACKAGE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

  @Singleton
  @Provides
  @WidgetsUrl
  fun provideWidgetsUrl(): String = "ag/getWidgets?limit=25"

  @Singleton
  @Provides
  @DefaultEditorialUrl
  fun provideDefaultEditorialUrl(): String =
    "${BuildConfig.STORE_DOMAIN}user/action/item/cards/get/type=CURATION_1/limit=10/store_name=${BuildConfig.MARKET_NAME}/aptoide_uid=1/?subtype=COLLECTION&aab=1&aptoide_vercode=${BuildConfig.VERSION_CODE}"

  @Singleton
  @Provides
  @StoreName
  fun provideStoreName(): String = BuildConfig.MARKET_NAME.lowercase(Locale.ROOT)

  @Singleton
  @Provides
  @StoreDomain
  fun provideEnvironmentDomain(): String = BuildConfig.STORE_DOMAIN

  @Singleton
  @Provides
  @MMPDomain
  fun provideMMPDomain(): String = BuildConfig.APTOIDE_WEB_SERVICES_MMP_HOST

  @Provides
  @Singleton
  fun providesUserAgent(aptoideGetUserAgent: AptoideGetHeaders): GetUserAgent =
    aptoideGetUserAgent

  @Provides
  @Singleton
  fun providesAcceptLanguage(aptoideGetAcceptLanguage: AptoideGetHeaders): GetAcceptLanguage =
    aptoideGetAcceptLanguage

  @Singleton
  @Provides
  @PermissionsDataStore
  fun providePermissionsDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
    return appContext.dataStore
  }

  @Provides
  fun provideAppPermissionsManager(
    @PermissionsDataStore dataStore: DataStore<Preferences>,
  ): AppPermissionsManager {
    return AppPermissionsManager(dataStore)
  }

  @Singleton
  @Provides
  @NetworkPreferencesDataStore
  fun provideNetworkPreferencesDataStore(
    @ApplicationContext appContext: Context,
  ): DataStore<Preferences> {
    return appContext.networkPreferencesDataStore
  }

  @Singleton
  @Provides
  @AppLaunchDataStore
  fun provideAppLaunchDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
    return appContext.appLaunchDataStore
  }

  @Singleton
  @Provides
  fun provideAppLaunchPreferencesManager(
    @AppLaunchDataStore dataStore: DataStore<Preferences>,
  ): AppLaunchPreferencesManager {
    return AppLaunchPreferencesManager(dataStore)
  }

  @Singleton
  @Provides
  @ThemeDataStore
  fun provideThemeDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
    return appContext.themeDataStore
  }

  @Singleton
  @Provides
  fun provideThemePreferencesManager(
    @ThemeDataStore dataStore: DataStore<Preferences>,
  ): ThemePreferencesManager {
    return ThemePreferencesManager(dataStore)
  }

  @Singleton
  @Provides
  fun provideSearchStoreManager(): SearchStoreManager = AppGamesSearchStoreManager()

  @RetrofitBuzz
  @Provides
  @Singleton
  fun provideSearchAutoCompleteRetrofit(@BaseOkHttp okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(BuildConfig.SEARCH_BUZZ_DOMAIN)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }

  @Singleton
  @Provides
  fun provideAutoCompleteSuggestionsRepository(
    @RetrofitBuzz retrofitBuzz: Retrofit,
  ): AutoCompleteSuggestionsRepository {
    return AppGamesAutoCompleteSuggestionsRepository(
      retrofitBuzz.create(AutoCompleteSearchRetrofitService::class.java),
    )
  }

  @Singleton
  @Provides
  @VersionCode
  fun provideVersionCode(): Int = BuildConfig.VERSION_CODE

  @Provides
  @Singleton
  fun providesQLogicInterceptor(
    deviceInfo: DeviceInfo,
  ): QLogicInterceptor {
    return AptoideQLogicInterceptor(
      deviceInfo = deviceInfo,
    )
  }

  @Qualifier
  @Retention(AnnotationRetention.BINARY)
  annotation class ThemeDataStore

  @Singleton
  @Provides
  @FeatureFlagsDataStore
  fun provideUserFeatureFlagsDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> =
    appContext.userFeatureFlagsDataStore

  @Provides
  @Singleton
  fun provideAptoideFeatureFlagsRepository(): FeatureFlagsRepository =
    AptoideFeatureFlagsRepository()

  @Singleton
  @Provides
  @UninstallPackagesFilter
  fun providePackagesToFilter(): List<String> = listOf(
    BuildConfig.APPLICATION_ID,
    WALLET_PACKAGE_NAME,
    "cm.aptoide.pt"
  )
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PermissionsDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NetworkPreferencesDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppLaunchDataStore
