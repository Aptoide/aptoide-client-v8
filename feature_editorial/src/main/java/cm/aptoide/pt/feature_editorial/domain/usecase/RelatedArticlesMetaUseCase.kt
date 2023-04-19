package cm.aptoide.pt.feature_editorial.domain.usecase

import cm.aptoide.pt.feature_editorial.data.EditorialRepository
import cm.aptoide.pt.feature_editorial.domain.ArticleMeta
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ViewModelScoped
class RelatedArticlesMetaUseCase @Inject constructor(
  private val editorialRepository: EditorialRepository
) {
  fun getRelatedArticlesMeta(packageName: String): Flow<List<ArticleMeta>> =
    editorialRepository.getRelatedArticlesMeta(packageName)
      .map { result ->
        result.map { article ->
          ArticleMeta(
            id = article.id,
            title = article.title,
            url = article.url,
            caption = article.caption,
            summary = article.summary,
            image = article.image,
            subtype = article.subtype,
            date = article.date,
            views = article.views,
          )
        }
      }
}
