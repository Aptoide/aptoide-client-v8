package cm.aptoide.pt.download_view.di

import cm.aptoide.pt.aptoide_installer.InstallManager
import cm.aptoide.pt.download_view.domain.usecase.DownloadAppUseCase
import cm.aptoide.pt.download_view.domain.usecase.ObserveDownloadUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class UseCaseModule {

  @Provides
  fun provideDownloadAppUseCase(installManager: InstallManager): DownloadAppUseCase {
    return DownloadAppUseCase(installManager)
  }

  @Provides
  fun provideObserveDownloadUseCaseModule(installManager: InstallManager): ObserveDownloadUseCase {
    return ObserveDownloadUseCase(installManager)
  }
}