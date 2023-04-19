package cm.aptoide.pt.feature_editorial.domain.usecase

import cm.aptoide.pt.feature_editorial.data.EditorialRepository
import cm.aptoide.pt.feature_editorial.domain.Article
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class ArticleUseCase @Inject constructor(private val editorialRepository: EditorialRepository) {
  fun getDetails(widgetUrl: String): Flow<Article> {
    return editorialRepository.getArticle(widgetUrl)
  }
}