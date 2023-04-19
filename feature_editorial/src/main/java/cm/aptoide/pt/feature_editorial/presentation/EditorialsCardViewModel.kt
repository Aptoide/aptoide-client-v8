package cm.aptoide.pt.feature_editorial.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_editorial.domain.usecase.ArticlesMetaUseCase
import cm.aptoide.pt.feature_editorial.domain.usecase.RelatedArticlesMetaUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class EditorialsCardViewModel(
  editorialWidgetUrl: String,
  subtype: String?,
  articlesMetaUseCase: ArticlesMetaUseCase
) : ViewModel() {

  val adListId = UUID.randomUUID().toString()
  private val viewModelState =
    MutableStateFlow(EditorialsCardUiState(editorialsMetas = emptyList(), loading = true))

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      articlesMetaUseCase.getArticlesMeta(editorialWidgetUrl, subtype)
        .catch { e ->
          Timber.w(e)
          viewModelState.update { it.copy(loading = false) }
        }
        .collect { metaList ->
          viewModelState.update {
            it.copy(
              editorialsMetas = metaList,
              loading = false
            )
          }
        }
    }
  }
}

class RelatedEditorialsCardViewModel(
  packageName: String,
  relatedArticlesMetaUseCase: RelatedArticlesMetaUseCase
) : ViewModel() {

  val adListId = UUID.randomUUID().toString()
  private val viewModelState =
    MutableStateFlow(EditorialsCardUiState(editorialsMetas = emptyList(), loading = true))

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      relatedArticlesMetaUseCase.getRelatedArticlesMeta(packageName)
        .catch { e ->
          Timber.w(e)
          viewModelState.update { it.copy(loading = false) }
        }
        .collect { metaList ->
          viewModelState.update {
            it.copy(
              editorialsMetas = metaList,
              loading = false
            )
          }
        }
    }
  }
}
