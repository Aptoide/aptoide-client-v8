package com.aptoide.android.aptoidegames.notifications.di

import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.notifications.analytics.FirebaseNotificationAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NotificationsModule {

  @Singleton
  @Provides
  fun providesFirebaseNotificationAnalytics(biAnalytics: BIAnalytics): FirebaseNotificationAnalytics =
    FirebaseNotificationAnalytics(biAnalytics = biAnalytics)
}
