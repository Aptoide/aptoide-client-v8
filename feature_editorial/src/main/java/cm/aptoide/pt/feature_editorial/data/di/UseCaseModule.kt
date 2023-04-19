package cm.aptoide.pt.feature_editorial.data.di

import cm.aptoide.pt.feature_editorial.domain.usecase.ArticlesMetaUseCase
import cm.aptoide.pt.feature_editorial.domain.usecase.ArticleUseCase
import cm.aptoide.pt.feature_editorial.domain.usecase.RelatedArticlesMetaUseCase
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
    articleUseCase: ArticleUseCase,
    articlesMetaUseCase: ArticlesMetaUseCase,
    relatedArticlesMetaUseCase: RelatedArticlesMetaUseCase
  ): EditorialDependenciesProvider =
    object : EditorialDependenciesProvider {
      override val articlesMetaUseCase: ArticlesMetaUseCase = articlesMetaUseCase
      override val articleUseCase: ArticleUseCase = articleUseCase
      override val relatedArticlesMetaUseCase: RelatedArticlesMetaUseCase =
        relatedArticlesMetaUseCase
    }
}