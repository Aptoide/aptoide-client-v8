package cm.aptoide.pt.di

import android.content.Context
import cm.aptoide.pt.analytics.AnalyticsInfoProvider
import cm.aptoide.pt.feature_categories.analytics.AptoideAnalyticsInfoProvider
import cm.aptoide.pt.feature_categories.analytics.AptoideFirebaseInfoProvider
import cm.aptoide.pt.firebase.FirebaseInfoProvider
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.installations.FirebaseInstallations
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

  @Singleton
  @Provides
  fun provideAnalyticsInfoProvider(): AptoideAnalyticsInfoProvider = AnalyticsInfoProvider()

  @Singleton
  @Provides
  fun provideFirebaseInfoProvider(): AptoideFirebaseInfoProvider = FirebaseInfoProvider()

  @Singleton
  @Provides
  fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics =
    FirebaseAnalytics.getInstance(context)

  @Singleton
  @Provides
  fun provideFirebaseInstallations(): FirebaseInstallations =
    FirebaseInstallations.getInstance()
}
