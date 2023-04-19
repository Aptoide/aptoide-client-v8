package cm.aptoide.pt.feature_editorial.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_editorial.domain.Article
import cm.aptoide.pt.feature_editorial.domain.usecase.ArticleUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

class EditorialViewModel(
  private val articleId: String,
  private val editorialUrl: String,
  private val articleUseCase: ArticleUseCase,
) :
  ViewModel() {
  private val viewModelState = MutableStateFlow(EditorialDetailViewModelState())
  val uiState = viewModelState.map { it.toUiState() }
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value.toUiState()
    )

  init {
    reload()
  }

  fun reload() {
    viewModelScope.launch {
      viewModelState.update { it.copy(type = EditorialUiStateType.LOADING) }
      articleUseCase.getDetails(editorialUrl)
        .catch { e ->
          Timber.w(e)
          viewModelState.update {
            it.copy(
              type = when (e) {
                is IOException -> EditorialUiStateType.NO_CONNECTION
                else -> EditorialUiStateType.ERROR
              }
            )
          }
        }
        .collect { result ->
          viewModelState.update {
            it.copy(
              article = result,
              type = EditorialUiStateType.IDLE,
            )
          }
        }
    }
  }

  fun onAppLoaded(app: App) {
    viewModelScope.launch {
      app.campaigns?.sendImpressionEvent()
    }
  }


  private data class EditorialDetailViewModelState(
    val article: Article? = null,
    val type: EditorialUiStateType = EditorialUiStateType.IDLE,
  ) {

    fun toUiState(): EditorialUiState =
      EditorialUiState(
        article = article,
        type = type
      )
  }
}
