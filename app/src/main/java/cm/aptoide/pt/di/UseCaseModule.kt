package cm.aptoide.pt.di

import cm.aptoide.pt.appview.AptoideTabsListProvider
import cm.aptoide.pt.download_view.presentation.InstallAppUseCaseProvider
import cm.aptoide.pt.feature_appview.presentation.TabsListProvider
import cm.aptoide.pt.install_manager.InstallManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class UseCaseModule {

  @Provides
  fun provideInstallAppUseCaseProvider(
    installManager: InstallManager,
  ): InstallAppUseCaseProvider =
    object : InstallAppUseCaseProvider {
      override val installManager: InstallManager = installManager
    }

  @Provides
  fun provideTabsList(): TabsListProvider {
    return AptoideTabsListProvider()
  }
}