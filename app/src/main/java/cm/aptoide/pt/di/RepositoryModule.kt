package cm.aptoide.pt.di

import cm.aptoide.pt.BuildConfig
import cm.aptoide.pt.apps.WidgetsNetworkService
import cm.aptoide.pt.aptoide_network.di.RetrofitV7
import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.feature_apps.data.network.service.WidgetsRemoteService
import cm.aptoide.pt.home.BottomNavigationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

  @Singleton
  @Provides
  fun provideBottomNavigationManager(): BottomNavigationManager = BottomNavigationManager()

  @Singleton
  @Provides
  @StoreName
  fun provideStoreName(): String = BuildConfig.MARKET_NAME

  @Provides
  @Singleton
  fun providesWidgetsRemoteService(
    @RetrofitV7 retrofitV7: Retrofit,
    @StoreName storeName: String
  ): WidgetsRemoteService = WidgetsNetworkService(
    widgetsRemoteDataSource = retrofitV7.create(WidgetsNetworkService.Retrofit::class.java),
    storeName = storeName
  )
}