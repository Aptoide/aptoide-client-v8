package com.aptoide.android.aptoidegames.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aptoide.android.aptoidegames.home.domain.AppThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppThemeViewModel @Inject constructor(
  private val appThemeUseCase: AppThemeUseCase,
) : ViewModel() {

  private val viewModelState = MutableStateFlow<Boolean?>(null)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      appThemeUseCase.isDarkTheme()
        .catch { throwable -> throwable.printStackTrace() }
        .collect { isAllowed -> viewModelState.update { isAllowed } }
    }
  }

  fun setSystem() {
    viewModelScope.launch {
      appThemeUseCase.setSystemDefault()
    }
  }

  fun setLight() {
    viewModelScope.launch {
      appThemeUseCase.setLightTheme()
    }
  }

  fun setDark() {
    viewModelScope.launch {
      appThemeUseCase.setDarkTheme()
    }
  }
}
