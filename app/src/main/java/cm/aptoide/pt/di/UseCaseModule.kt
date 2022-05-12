package cm.aptoide.pt.di

import cm.aptoide.pt.feature_apps.data.BundlesRepository
import cm.aptoide.pt.feature_apps.domain.GetHomeBundlesListUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

  @Provides
  @ViewModelScoped
  fun providesGetHomeBundlesListUseCase(bundlesRepository: BundlesRepository): GetHomeBundlesListUseCase {
    return GetHomeBundlesListUseCase(bundlesRepository)
  }
}