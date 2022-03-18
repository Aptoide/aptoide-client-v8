package cm.aptoide.pt.feature_updates.di

import cm.aptoide.pt.feature_updates.domain.repository.UpdatesRepository
import cm.aptoide.pt.feature_updates.domain.usecase.GetInstalledAppsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

  @Provides
  fun provideGetInstalledAppsUseCase(updatesRepository: UpdatesRepository): GetInstalledAppsUseCase {
    return GetInstalledAppsUseCase(updatesRepository)
  }
}