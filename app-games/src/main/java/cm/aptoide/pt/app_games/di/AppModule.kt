package cm.aptoide.pt.app_games.di

import cm.aptoide.pt.app_games.analytics.AnalyticsInfoProvider
import cm.aptoide.pt.app_games.firebase.FirebaseInfoProvider
import cm.aptoide.pt.feature_categories.analytics.AptoideAnalyticsInfoProvider
import cm.aptoide.pt.feature_categories.analytics.AptoideFirebaseInfoProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
  @Singleton
  @Provides
  fun provideFirebaseInfoProvider(): AptoideFirebaseInfoProvider = FirebaseInfoProvider()

  @Singleton
  @Provides
  fun provideAnalyticsInfoProvider(): AptoideAnalyticsInfoProvider = AnalyticsInfoProvider()
}
