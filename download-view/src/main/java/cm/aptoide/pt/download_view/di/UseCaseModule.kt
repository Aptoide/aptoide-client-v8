package cm.aptoide.pt.download_view.di

import android.content.Context
import cm.aptoide.pt.aptoide_installer.InstallManager
import cm.aptoide.pt.download_view.domain.usecase.CancelDownloadUseCase
import cm.aptoide.pt.download_view.domain.usecase.DownloadAppUseCase
import cm.aptoide.pt.download_view.domain.usecase.ObserveDownloadUseCase
import cm.aptoide.pt.download_view.domain.usecase.OpenAppUseCase
import cm.aptoide.pt.download_view.presentation.InstalledAppOpener
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
class UseCaseModule {

  @Provides
  fun provideDownloadAppUseCase(installManager: InstallManager): DownloadAppUseCase {
    return DownloadAppUseCase(installManager)
  }

  @Provides
  fun provideObserveDownloadUseCase(installManager: InstallManager): ObserveDownloadUseCase {
    return ObserveDownloadUseCase(installManager)
  }

  @Provides
  fun provideCancelDownloadUseCase(installManager: InstallManager): CancelDownloadUseCase {
    return CancelDownloadUseCase(installManager)
  }

  @Provides
  fun provideOpenAppUseCase(installedAppOpener: InstalledAppOpener): OpenAppUseCase {
    return OpenAppUseCase(installedAppOpener)
  }

  @Provides
  fun provideInstalledAppOpener(@ApplicationContext context: Context): InstalledAppOpener {
    return InstalledAppOpener(context)
  }
}