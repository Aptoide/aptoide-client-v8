package cm.aptoide.pt.feature_editorial.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_editorial.domain.ArticleDetail
import cm.aptoide.pt.feature_editorial.domain.usecase.GetEditorialDetailUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

class EditorialViewModel(
  private val articleId: String,
  private val getEditorialDetailUseCase: GetEditorialDetailUseCase,
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
      viewModelState.update { it.copy(type = EditorialDetailUiStateType.LOADING) }
      getEditorialDetailUseCase.getEditorialInfo(articleId)
        .catch { e ->
          Timber.w(e)
          viewModelState.update {
            it.copy(
              type = when (e) {
                is IOException -> EditorialDetailUiStateType.NO_CONNECTION
                else -> EditorialDetailUiStateType.ERROR
              }
            )
          }
        }
        .collect { result ->
          viewModelState.update {
            it.copy(article = result, type = EditorialDetailUiStateType.IDLE)
          }
        }
    }
  }


  private data class EditorialDetailViewModelState(
    val article: ArticleDetail? = null,
    val type: EditorialDetailUiStateType = EditorialDetailUiStateType.IDLE,
  ) {

    fun toUiState(): EditorialDetailUiState =
      EditorialDetailUiState(
        article = article,
        type = type,
      )
  }
}
