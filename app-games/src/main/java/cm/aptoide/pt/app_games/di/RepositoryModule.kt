package cm.aptoide.pt.app_games.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import cm.aptoide.pt.app_games.BuildConfig
import cm.aptoide.pt.app_games.feature_flags.AptoideFeatureFlagsRepository
import cm.aptoide.pt.app_games.home.repository.ThemePreferencesManager
import cm.aptoide.pt.app_games.network.AptoideGetUserAgent
import cm.aptoide.pt.app_games.network.AptoideQLogicInterceptor
import cm.aptoide.pt.app_games.search.repository.AppGamesAutoCompleteSuggestionsRepository
import cm.aptoide.pt.app_games.search.repository.AppGamesAutoCompleteSuggestionsRepository.AutoCompleteSearchRetrofitService
import cm.aptoide.pt.app_games.search.repository.AppGamesSearchStoreManager
import cm.aptoide.pt.app_games.themeDataStore
import cm.aptoide.pt.app_games.userFeatureFlagsDataStore
import cm.aptoide.pt.aptoide_network.data.network.GetUserAgent
import cm.aptoide.pt.aptoide_network.data.network.QLogicInterceptor
import cm.aptoide.pt.aptoide_network.di.BaseOkHttp
import cm.aptoide.pt.aptoide_network.di.RetrofitBuzz
import cm.aptoide.pt.aptoide_network.di.StoreDomain
import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.aptoide_network.di.VersionCode
import cm.aptoide.pt.environment_info.DeviceInfo
import cm.aptoide.pt.feature_campaigns.data.CampaignUrlNormalizer
import cm.aptoide.pt.feature_editorial.di.DefaultEditorialUrl
import cm.aptoide.pt.feature_flags.data.FeatureFlagsRepository
import cm.aptoide.pt.feature_flags.di.FeatureFlagsDataStore
import cm.aptoide.pt.feature_home.di.WidgetsUrl
import cm.aptoide.pt.feature_search.data.AutoCompleteSuggestionsRepository
import cm.aptoide.pt.feature_search.domain.repository.SearchStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import javax.inject.Qualifier
import javax.inject.Singleton
import cm.aptoide.pt.app_games.appLaunchDataStore
import cm.aptoide.pt.app_games.launch.AppLaunchPreferencesManager
import cm.aptoide.pt.app_games.networkPreferencesDataStore
import cm.aptoide.pt.app_games.notifications.NotificationsPermissionManager
import cm.aptoide.pt.app_games.dataStore
import cm.aptoide.pt.feature_oos.di.UninstallPackagesFilter

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

  @Singleton
  @Provides
  @WidgetsUrl
  fun provideWidgetsUrl(): String = "getStoreWidgets?limit=25"

  @Singleton
  @Provides
  @DefaultEditorialUrl
  fun provideDefaultEditorialUrl(): String =
    "${BuildConfig.STORE_DOMAIN}user/action/item/cards/get/type=CURATION_1/limit=10/store_name=${BuildConfig.MARKET_NAME}/aptoide_uid=0/?subtype=COLLECTION&aab=1&aptoide_vercode=${BuildConfig.VERSION_CODE}"

  @Singleton
  @Provides
  @StoreName
  fun provideStoreName(): String = BuildConfig.MARKET_NAME.lowercase(Locale.ROOT)

  @Singleton
  @Provides
  @StoreDomain
  fun provideEnvironmentDomain(): String = BuildConfig.STORE_DOMAIN

  @Provides
  @Singleton
  fun providesUserAgentInterceptor(aptoideGetUserAgent: AptoideGetUserAgent): GetUserAgent =
    aptoideGetUserAgent

  @Singleton
  @Provides
  @PermissionsDataStore
  fun providePermissionsDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
    return appContext.dataStore
  }

  @Provides
  fun provideNotificationsPermissionManager(
    @ApplicationContext context: Context,
    @PermissionsDataStore dataStore: DataStore<Preferences>,
  ): NotificationsPermissionManager {
    return NotificationsPermissionManager(context, dataStore)
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
  fun providesCampaignUrlNormalizer(@ApplicationContext context: Context): CampaignUrlNormalizer =
    CampaignUrlNormalizer(context)

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
  fun providePackagesToFilter(): List<String> =
    listOf(BuildConfig.APPLICATION_ID)
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
