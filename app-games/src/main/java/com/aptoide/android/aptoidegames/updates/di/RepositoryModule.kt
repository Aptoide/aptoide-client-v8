package com.aptoide.android.aptoidegames.updates.di

import android.content.Context
import cm.aptoide.pt.feature_updates.presentation.UpdatesNotificationProvider
import com.aptoide.android.aptoidegames.updates.UpdatesNotificationBuilder
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
  ): UpdatesNotificationProvider {
    return UpdatesNotificationBuilder(context)
  }
}
