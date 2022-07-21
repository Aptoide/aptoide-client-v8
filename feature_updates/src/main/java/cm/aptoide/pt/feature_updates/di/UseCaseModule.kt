package cm.aptoide.pt.feature_updates.di

import android.content.Context
import cm.aptoide.pt.feature_updates.domain.repository.UpdatesRepository
import cm.aptoide.pt.feature_updates.domain.usecase.GetInstalledAppsUseCase
import cm.aptoide.pt.feature_updates.domain.usecase.OpenInstalledAppUseCase
import cm.aptoide.pt.feature_updates.domain.usecase.UninstallAppUseCase
import cm.aptoide.pt.feature_updates.presentation.InstalledAppOpener
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
    installedAppOpener: InstalledAppOpener
  ): OpenInstalledAppUseCase {
    return OpenInstalledAppUseCase(installedAppOpener)
  }

  @Provides
  fun provideInstalledAppOpener(@ApplicationContext context: Context): InstalledAppOpener {
    return InstalledAppOpener(context)
  }

  @Provides
  fun provideUninstallAppUseCase(
    @ApplicationContext context: Context
  ): UninstallAppUseCase {
    return UninstallAppUseCase(context)
  }
}