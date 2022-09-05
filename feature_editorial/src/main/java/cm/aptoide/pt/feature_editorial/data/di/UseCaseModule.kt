package cm.aptoide.pt.feature_editorial.data.di

import cm.aptoide.pt.feature_editorial.data.EditorialRepository
import cm.aptoide.pt.feature_editorial.domain.usecase.GetEditorialDetailUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

  @Provides
  fun provideGetEditorialDetailUseCase(editorialRepository: EditorialRepository): GetEditorialDetailUseCase {
    return GetEditorialDetailUseCase(editorialRepository)
  }
}