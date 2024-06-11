package com.aptoide.android.aptoidegames.di

import cm.aptoide.pt.feature_categories.analytics.AptoideAnalyticsInfoProvider
import cm.aptoide.pt.feature_categories.analytics.AptoideFirebaseInfoProvider
import com.aptoide.android.aptoidegames.analytics.AnalyticsInfoProvider
import com.aptoide.android.aptoidegames.firebase.FirebaseInfoProvider
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
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
}
