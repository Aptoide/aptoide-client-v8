package cm.aptoide.pt.feature_editorial.domain.usecase

import cm.aptoide.pt.feature_editorial.data.EditorialRepository
import cm.aptoide.pt.feature_editorial.domain.Article
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class ArticleUseCase @Inject constructor(private val editorialRepository: EditorialRepository) {
  suspend fun getDetails(widgetUrl: String): Article =
    editorialRepository.getArticle(widgetUrl)
}