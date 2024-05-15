package com.aptoide.android.aptoidegames.feature_apps.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.download_view.presentation.InstalledAppOpener
import com.aptoide.android.aptoidegames.feature_apps.domain.GetInstalledMyGamesUseCase
import com.aptoide.android.aptoidegames.feature_apps.presentation.SeeAllMyGamesUiState.Loading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SeeAllMyGamesViewModel @Inject constructor(
  private val getInstalledMyGamesUseCase: GetInstalledMyGamesUseCase,
  private val installedAppOpener: InstalledAppOpener,
) : ViewModel() {

  private val viewModelState =
    MutableStateFlow<SeeAllMyGamesUiState>(Loading)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      getInstalledMyGamesUseCase.getMyGamesAppsList()
        .catch { e ->
          Timber.e(e)
          emit(emptyList())
        }
        .collect { list ->
          viewModelState.update {
            SeeAllMyGamesUiState.AppsList(list)
          }
        }
    }
  }

  fun openApp(packageName: String) {
    installedAppOpener.openInstalledApp(packageName)
  }
}
