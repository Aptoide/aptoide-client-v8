package cm.aptoide.pt.di

import cm.aptoide.pt.VanillaAppDetailsMapper
import cm.aptoide.pt.download_view.domain.model.AppDetailsMapper
import cm.aptoide.pt.download_view.domain.usecase.InstallAppUseCase
import cm.aptoide.pt.download_view.presentation.InstallAppUseCaseProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class UseCaseModule {

  @Provides
  fun provideAppDetailsMapper(): AppDetailsMapper<String> = VanillaAppDetailsMapper()

  @Provides
  fun provideInstallAppUseCaseProvider(
    installAppUseCase: InstallAppUseCase<String>
  ): InstallAppUseCaseProvider =
    object : InstallAppUseCaseProvider {
      override val installAppUseCase: InstallAppUseCase<*> = installAppUseCase
    }
}