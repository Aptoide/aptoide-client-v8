package cm.aptoide.pt.feature_editorial.data.di

import cm.aptoide.pt.feature_editorial.domain.usecase.EditorialsMetaUseCase
import cm.aptoide.pt.feature_editorial.presentation.EditorialsMetaUseCaseProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

  @Provides
  fun provideEditorialsMetaUseCaseProvider(
    editorialsMetaUseCase: EditorialsMetaUseCase
  ): EditorialsMetaUseCaseProvider =
    object : EditorialsMetaUseCaseProvider {
      override val editorialsMetaUseCase: EditorialsMetaUseCase = editorialsMetaUseCase
    }
}