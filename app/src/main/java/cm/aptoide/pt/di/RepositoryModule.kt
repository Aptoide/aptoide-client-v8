package cm.aptoide.pt.di

import cm.aptoide.pt.BuildConfig
import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.home.BottomNavigationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
}