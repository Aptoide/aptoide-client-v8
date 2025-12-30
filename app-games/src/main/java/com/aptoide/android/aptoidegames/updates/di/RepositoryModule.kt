package com.aptoide.android.aptoidegames.updates.di

import android.content.Context
import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import cm.aptoide.pt.feature_updates.data.StoreNameProvider
import cm.aptoide.pt.feature_updates.data.VIPUpdatesProvider
import cm.aptoide.pt.feature_updates.presentation.UpdatesNotificationProvider
import com.aptoide.android.aptoidegames.installer.notifications.ImageDownloader
import com.aptoide.android.aptoidegames.notifications.analytics.NotificationsAnalytics
import com.aptoide.android.aptoidegames.updates.presentation.AptoideUpdatesStoreNameProvider
import com.aptoide.android.aptoidegames.updates.presentation.AptoideVIPUpdatesProvider
import com.aptoide.android.aptoidegames.updates.presentation.UpdatesNotificationBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

  @Provides
  @Singleton
  fun providesUpdatesNotificationProvider(
    @ApplicationContext context: Context,
    imageDownloader: ImageDownloader,
    notificationsAnalytics: NotificationsAnalytics
  ): UpdatesNotificationProvider {
    return UpdatesNotificationBuilder(context, imageDownloader, notificationsAnalytics)
  }

  @Provides
  @Singleton
  fun provideVIPUpdatesProvider(featureFlags: FeatureFlags): VIPUpdatesProvider =
    AptoideVIPUpdatesProvider(featureFlags)

  @Provides
  @Singleton
  fun provideStoreNameProvider(
    featureFlags: FeatureFlags,
    @StoreName defaultStoreName: String,
  ): StoreNameProvider = AptoideUpdatesStoreNameProvider(featureFlags, defaultStoreName)
}
