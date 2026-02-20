package com.aptoide.android.aptoidegames.feature_rtb.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aptoide.android.aptoidegames.feature_rtb.data.RTBAppsListUiState
import com.aptoide.android.aptoidegames.feature_rtb.repository.RTBRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

class RTBAppListViewModel(
  private val repository: RTBRepository,
  private val tag: String,
) : ViewModel() {

  private val viewModelState = MutableStateFlow<RTBAppsListUiState>(RTBAppsListUiState.Loading)

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
      viewModelState.update { RTBAppsListUiState.Loading }
      try {
        val result = repository.getRTBApps(tag)
        viewModelState.update {
          if (result.isEmpty()) {
            RTBAppsListUiState.Empty
          } else {
            RTBAppsListUiState.Idle(apps = result)
          }
        }
      } catch (t: Throwable) {
        Timber.w(t)
        viewModelState.update {
          when (t) {
            is IOException -> RTBAppsListUiState.NoConnection
            else -> RTBAppsListUiState.Error
          }
        }
      }
    }
  }
}
