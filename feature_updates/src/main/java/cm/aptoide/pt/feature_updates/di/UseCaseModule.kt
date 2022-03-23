package cm.aptoide.pt.feature_updates.di

import android.content.Context
import cm.aptoide.pt.feature_updates.domain.repository.UpdatesRepository
import cm.aptoide.pt.feature_updates.domain.usecase.GetInstalledAppsUseCase
import cm.aptoide.pt.feature_updates.domain.usecase.OpenInstalledAppUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

  @Provides
  fun provideGetInstalledAppsUseCase(updatesRepository: UpdatesRepository): GetInstalledAppsUseCase {
    return GetInstalledAppsUseCase(updatesRepository)
  }

  @Provides
  fun provideOpenInstalledAppUseCase(
    @ApplicationContext context: Context
  ): OpenInstalledAppUseCase {
    return OpenInstalledAppUseCase(context)
  }
}