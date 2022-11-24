package cm.aptoide.pt.feature_editorial.domain.usecase

import cm.aptoide.pt.feature_editorial.data.EditorialRepository
import cm.aptoide.pt.feature_editorial.domain.EditorialMeta
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ViewModelScoped
class EditorialsMetaUseCase @Inject constructor(
  private val editorialRepository: EditorialRepository
) {
  fun getEditorialsMeta(editorialWidgetUrl: String): Flow<List<EditorialMeta>> =
    editorialRepository.getArticleMeta(editorialWidgetUrl)
      .map { editorialResult ->
        if (editorialResult is EditorialRepository.EditorialResult.Success) {
          editorialResult.data.map { article ->
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
        } else {
          throw IllegalStateException()
        }
      }
}
