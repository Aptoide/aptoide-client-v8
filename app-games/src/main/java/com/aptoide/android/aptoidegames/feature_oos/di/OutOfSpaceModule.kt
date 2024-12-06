package com.aptoide.android.aptoidegames.feature_oos.di

import cm.aptoide.pt.aptoide_network.di.StoreName
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.feature_oos.analytics.OutOfSpaceAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class OutOfSpaceModule {

  @Singleton
  @Provides
  fun providesOutOfSpaceAnalytics(
    genericAnalytics: GenericAnalytics,
    biAnalytics: BIAnalytics,
    @StoreName storeName: String,
  ): OutOfSpaceAnalytics = OutOfSpaceAnalytics(genericAnalytics, biAnalytics, storeName)
}
