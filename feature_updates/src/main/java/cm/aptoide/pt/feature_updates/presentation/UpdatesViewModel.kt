package cm.aptoide.pt.feature_updates.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_apps.data.randomApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.random.Random
import kotlin.random.nextInt

@HiltViewModel
//TODO Add real request/arguments
class UpdatesViewModel @Inject constructor(

) :
  ViewModel() {

  private val viewModelState = MutableStateFlow<UpdatesUiState>(UpdatesUiState.Loading)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    reload()
  }

  fun reload() {
    viewModelScope.launch {
      viewModelState.update { UpdatesUiState.Loading }
      try {
        //TODO add real request
        val result = List(Random.nextInt(0..5)) { randomApp }
        viewModelState.update {
          if (result.isEmpty()) {
            UpdatesUiState.Empty
          } else {
            UpdatesUiState.Idle(updatesList = result)
          }
        }
      } catch (t: Throwable) {
        Timber.w(t)
        //No Errors For now
        viewModelState.update {
          UpdatesUiState.Empty
        }
      }
    }
  }
}

