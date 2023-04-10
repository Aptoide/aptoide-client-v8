package cm.aptoide.pt.feature_categories.data.di

import cm.aptoide.pt.feature_categories.domain.usecase.GetCategoriesListUseCase
import cm.aptoide.pt.feature_categories.presentation.CategoriesDependenciesProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

  @Provides
  fun provideCategoriesUseCaseProvider(
    getEditorialDetailUseCase: GetCategoriesListUseCase,
  ): CategoriesDependenciesProvider =
    object : CategoriesDependenciesProvider {
      override val getCategoriesListUseCase: GetCategoriesListUseCase = getEditorialDetailUseCase
    }
}
