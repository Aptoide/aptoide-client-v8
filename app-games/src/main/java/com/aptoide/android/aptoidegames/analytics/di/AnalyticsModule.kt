package com.aptoide.android.aptoidegames.analytics.di

import android.content.Context
import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.feature_categories.analytics.AptoideAnalyticsInfoProvider
import cm.aptoide.pt.feature_categories.analytics.AptoideFirebaseInfoProvider
import com.aptoide.android.aptoidegames.analytics.AnalyticsInfoProvider
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.FirebaseAnalyticsSender
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.analytics.IndicativeAnalyticsSender
import com.aptoide.android.aptoidegames.firebase.FirebaseInfoProvider
import com.aptoide.android.aptoidegames.installer.analytics.InstallAnalytics
import com.google.firebase.analytics.FirebaseAnalytics
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
class AnalyticsModule {
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
  fun provideBiAnalytics(indicativeAnalyticsSender: IndicativeAnalyticsSender): BIAnalytics =
    BIAnalytics(indicativeAnalyticsSender)

  @Singleton
  @Provides
  fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics =
    FirebaseAnalytics.getInstance(context)

  @Singleton
  @Provides
  fun providesGenericAnalytics(firebaseAnalyticsSender: FirebaseAnalyticsSender): GenericAnalytics =
    GenericAnalytics(firebaseAnalyticsSender)

  @Singleton
  @Provides
  fun providesInstallAnalytics(
    genericAnalytics: GenericAnalytics,
    biAnalytics: BIAnalytics,
    @StoreName storeName: String,
  ): InstallAnalytics = InstallAnalytics(genericAnalytics, biAnalytics, storeName)
}
