package cm.aptoide.pt.feature_updates.di

import cm.aptoide.pt.feature_updates.data.AptoideUpdatesRepository
import cm.aptoide.pt.feature_updates.domain.repository.UpdatesRepository
import cm.aptoide.pt.installedapps.data.database.LocalInstalledAppsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

  @Singleton
  @Provides
  fun provideUpdatesRepository(installedAppsRepository: LocalInstalledAppsRepository): UpdatesRepository {
    return AptoideUpdatesRepository(installedAppsRepository)
  }

}