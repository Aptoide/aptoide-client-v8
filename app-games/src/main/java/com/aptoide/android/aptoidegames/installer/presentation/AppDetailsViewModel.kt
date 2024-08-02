package com.aptoide.android.aptoidegames.installer.presentation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aptoide.android.aptoidegames.installer.AppDetailsUseCase
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_apps.data.App
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppDetailsViewModel @Inject constructor(
  private val appDetailsUseCase: AppDetailsUseCase,
) : ViewModel() {
  fun saveAppDetails(app: App, onSaved: () -> Unit) {
    viewModelScope.launch {
      appDetailsUseCase.setAppDetails(app)
    }.invokeOnCompletion { onSaved() }
  }

  suspend fun saveAppDetailsBlocking(app: App) {
    appDetailsUseCase.setAppDetails(app)
  }
}

@Composable
fun rememberSaveAppDetails(): Pair<(App, () -> Unit) -> Unit, suspend (App) -> Unit> =
  runPreviewable(
    preview = {
      { _: App, _: () -> Unit -> } to {}
    },
    real = {
      val appDetailsViewModel = hiltViewModel<AppDetailsViewModel>()
      appDetailsViewModel::saveAppDetails to appDetailsViewModel::saveAppDetailsBlocking
    },
  )
