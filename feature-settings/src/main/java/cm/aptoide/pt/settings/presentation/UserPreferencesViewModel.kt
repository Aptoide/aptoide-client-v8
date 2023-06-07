package cm.aptoide.pt.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.settings.domain.FlagPreferencesUseCase
import cm.aptoide.pt.settings.domain.IntPreferencesUseCase
import cm.aptoide.pt.settings.domain.StringPreferencesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FlagViewModel(
  private val flagPreferencesUseCase: FlagPreferencesUseCase,
) : ViewModel() {

  private val viewModelState = MutableStateFlow<Boolean?>(null)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    // APP THEME
    viewModelScope.launch {
      flagPreferencesUseCase.get()
        .catch { throwable -> throwable.printStackTrace() }
        .collect { flag -> viewModelState.update { flag } }
    }
  }

  fun setFlag(flag: Boolean?) {
    viewModelScope.launch {
      flagPreferencesUseCase.set(flag)
    }
  }
}

class StringViewModel(
  private val stringPreferencesUseCase: StringPreferencesUseCase,
) : ViewModel() {

  private val viewModelState = MutableStateFlow("")

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      stringPreferencesUseCase.get()
        .catch { throwable -> throwable.printStackTrace() }
        .collect { str -> viewModelState.update { str } }
    }
  }

  fun setString(str: String) {
    viewModelScope.launch {
      stringPreferencesUseCase.set(str)
    }
  }
}

class IntViewModel(
  private val intPreferencesUseCase: IntPreferencesUseCase,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(300)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      intPreferencesUseCase.get()
        .catch { throwable -> throwable.printStackTrace() }
        .collect { value -> viewModelState.update { value } }
    }
  }

  fun setValue(value: Int) {
    viewModelScope.launch {
      intPreferencesUseCase.set(value)
    }
  }
}
