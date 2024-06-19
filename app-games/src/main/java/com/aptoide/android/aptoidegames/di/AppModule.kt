package com.aptoide.android.aptoidegames.di

import android.content.Context
import cm.aptoide.pt.feature_categories.analytics.AptoideAnalyticsInfoProvider
import cm.aptoide.pt.feature_categories.analytics.AptoideFirebaseInfoProvider
import cm.aptoide.pt.install_manager.InstallManager
import com.aptoide.android.aptoidegames.analytics.AnalyticsInfoProvider
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.firebase.FirebaseInfoProvider
import com.aptoide.android.aptoidegames.launch.AppLaunchPreferencesManager
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
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
  fun provideFirebaseInfoProvider(
    firebaseMessaging: FirebaseMessaging,
  ): AptoideFirebaseInfoProvider {
    return FirebaseInfoProvider(
      firebaseMessaging = firebaseMessaging
    )
  }

  @Singleton
  @Provides
  fun provideAnalyticsInfoProvider(
    firebaseInstallations: FirebaseInstallations,
  ): AptoideAnalyticsInfoProvider = AnalyticsInfoProvider(
    firebaseInstallations = firebaseInstallations
  )

  @Singleton
  @Provides
  fun provideBiAnalytics(
    installManager: InstallManager,
    appLaunchPreferencesManager: AppLaunchPreferencesManager,
    @ApplicationContext context: Context
  ): BIAnalytics = BIAnalytics(
    installManager = installManager,
    appLaunchPreferencesManager = appLaunchPreferencesManager,
    context = context
  )
}
