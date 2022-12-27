package cm.aptoide.pt.feature_editorial.data.di

import cm.aptoide.pt.feature_editorial.domain.usecase.EditorialsMetaUseCase
import cm.aptoide.pt.feature_editorial.domain.usecase.GetEditorialDetailUseCase
import cm.aptoide.pt.feature_editorial.domain.usecase.RelatedEditorialsMetaUseCase
import cm.aptoide.pt.feature_editorial.presentation.EditorialDependenciesProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

  @Provides
  fun provideEditorialsMetaUseCaseProvider(
    getEditorialDetailUseCase: GetEditorialDetailUseCase,
    editorialsMetaUseCase: EditorialsMetaUseCase,
    relatedEditorialsMetaUseCase: RelatedEditorialsMetaUseCase
  ): EditorialDependenciesProvider =
    object : EditorialDependenciesProvider {
      override val editorialsMetaUseCase: EditorialsMetaUseCase = editorialsMetaUseCase
      override val getEditorialDetailUseCase: GetEditorialDetailUseCase = getEditorialDetailUseCase
      override val relatedEditorialsMetaUseCase: RelatedEditorialsMetaUseCase =
        relatedEditorialsMetaUseCase
    }
}