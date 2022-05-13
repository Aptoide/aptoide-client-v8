package cm.aptoide.pt.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

  /*@Provides
  @ViewModelScoped
  fun providesGetHomeBundlesListUseCase(bundlesRepository: BundlesRepository): GetHomeBundlesListUseCase {
    return GetHomeBundlesListUseCase(bundlesRepository)
  }*/
}