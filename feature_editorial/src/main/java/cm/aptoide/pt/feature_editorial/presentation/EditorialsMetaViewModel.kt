package cm.aptoide.pt.feature_editorial.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_editorial.domain.usecase.EditorialsMetaUseCase
import cm.aptoide.pt.feature_editorial.domain.usecase.RelatedEditorialsMetaUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class EditorialsMetaViewModel(
  editorialWidgetUrl: String,
  subtype: String?,
  editorialsMetaUseCase: EditorialsMetaUseCase
) : ViewModel() {

  val adListId = UUID.randomUUID().toString()
  private val viewModelState =
    MutableStateFlow(EditorialsMetaUiState(editorialsMetas = emptyList(), loading = true))

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      editorialsMetaUseCase.getEditorialsMeta(editorialWidgetUrl, subtype)
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

class RelatedEditorialsMetaViewModel(
  packageName: String,
  relatedEditorialsMetaUseCase: RelatedEditorialsMetaUseCase
) : ViewModel() {

  val adListId = UUID.randomUUID().toString()
  private val viewModelState =
    MutableStateFlow(EditorialsMetaUiState(editorialsMetas = emptyList(), loading = true))

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      relatedEditorialsMetaUseCase.getEditorialsMeta(packageName)
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
