package cm.aptoide.pt.di

import cm.aptoide.pt.analytics.AnalyticsInfoProvider
import cm.aptoide.pt.feature_apps.data.network.analytics.AptoideAnalyticsInfoProvider
import cm.aptoide.pt.feature_apps.data.network.firebase.AptoideFirebaseInfoProvider
import cm.aptoide.pt.firebase.FirebaseInfoProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

  @Singleton
  @Provides
  fun provideAnalyticsInfoProvider() : AptoideAnalyticsInfoProvider = AnalyticsInfoProvider()

  @Singleton
  @Provides
  fun provideFirebaseInfoProvider() : AptoideFirebaseInfoProvider = FirebaseInfoProvider()
}
