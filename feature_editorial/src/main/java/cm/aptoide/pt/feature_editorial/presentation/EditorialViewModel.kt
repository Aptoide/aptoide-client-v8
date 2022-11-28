package cm.aptoide.pt.feature_editorial.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_editorial.data.ArticleDetail
import cm.aptoide.pt.feature_editorial.data.EditorialRepository
import cm.aptoide.pt.feature_editorial.domain.usecase.GetEditorialDetailUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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
    viewModelScope.launch {
      getEditorialDetailUseCase.getEditorialInfo(articleId)
        .catch { throwable ->
          throwable.printStackTrace()
        }.collect { editorialResult ->
          viewModelState.update {
            when (editorialResult) {
              is EditorialRepository.EditorialDetailResult.Success -> it.copy(
                article = editorialResult.data,
                isLoading = false
              )
              is EditorialRepository.EditorialDetailResult.Error -> it.copy()
            }
          }
        }
    }
  }


  private data class EditorialDetailViewModelState(
    val article: ArticleDetail? = null,
    val isLoading: Boolean = false,
  ) {

    fun toUiState(): EditorialDetailUiState =
      EditorialDetailUiState(
        article,
        isLoading,
      )
  }
}
