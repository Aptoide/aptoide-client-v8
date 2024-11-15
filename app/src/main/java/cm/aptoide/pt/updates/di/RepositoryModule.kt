package cm.aptoide.pt.updates.di

import cm.aptoide.pt.feature_updates.presentation.UpdatesNotificationProvider
import cm.aptoide.pt.updates.FakeUpdatesNotificationProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

  @Provides
  @Singleton
  fun providesUpdatesNotificationProvider(): UpdatesNotificationProvider =
    FakeUpdatesNotificationProvider()
}
