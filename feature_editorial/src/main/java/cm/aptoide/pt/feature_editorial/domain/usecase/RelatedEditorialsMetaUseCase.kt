package cm.aptoide.pt.feature_editorial.domain.usecase

import cm.aptoide.pt.feature_editorial.data.EditorialRepository
import cm.aptoide.pt.feature_editorial.domain.EditorialMeta
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ViewModelScoped
class RelatedEditorialsMetaUseCase @Inject constructor(
  private val editorialRepository: EditorialRepository
) {
  fun getEditorialsMeta(packageName: String): Flow<List<EditorialMeta>> =
    editorialRepository.getRelatedArticlesMeta(packageName)
      .map { result ->
        result.map { article ->
          EditorialMeta(
            id = article.id,
            title = article.title,
            summary = article.summary,
            image = article.image,
            subtype = article.subtype,
            date = article.date,
            views = article.views,
          )
        }
      }
}
