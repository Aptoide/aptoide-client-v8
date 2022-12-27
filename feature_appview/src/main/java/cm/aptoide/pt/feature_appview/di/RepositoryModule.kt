package cm.aptoide.pt.feature_appview.di

import cm.aptoide.pt.feature_apps.data.AppsRepository
import cm.aptoide.pt.feature_appview.data.AptoideAppViewRepository
import cm.aptoide.pt.feature_appview.domain.repository.AppViewRepository
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
  fun provideAppViewRepository(appsRepository: AppsRepository): AppViewRepository =
    AptoideAppViewRepository(appsRepository)
}