package com.aptoide.android.aptoidegames.feature_apps.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.download_view.presentation.InstalledAppOpener
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_apps.data.MyGamesApp
import cm.aptoide.pt.feature_apps.data.randomMyGamesApp
import com.aptoide.android.aptoidegames.feature_apps.domain.GetInstalledMyGamesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val getInstalledMyGamesUseCase: GetInstalledMyGamesUseCase,
  val installedAppOpener: InstalledAppOpener
) : ViewModel()

@Composable
fun rememberMyGamesBundleUIState(): Triple<MyGamesBundleUiState, () -> Unit, (packageName: String) -> Unit> =
  runPreviewable(
    preview = {
      Triple(MyGamesBundleUiState.AppsList(List((0..50).random()) { randomMyGamesApp }), {}, {})
    }, real = {
    val injectionsProvider = hiltViewModel<InjectionsProvider>()
    val vm: MyGamesBundleViewModel = viewModel(
      key = "mygamesbundleviewmodel",
      factory = object : Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          @Suppress("UNCHECKED_CAST")
          return MyGamesBundleViewModel(
            getInstalledMyGamesUseCase = injectionsProvider.getInstalledMyGamesUseCase,
            installedAppOpener = injectionsProvider.installedAppOpener
          ) as T
        }
      }
    )
    val uiState by vm.uiState.collectAsState()
    Triple(uiState, vm::retry, vm::openApp)
  })

@HiltViewModel
class MyGamesBundleViewModel @Inject constructor(
  private val getInstalledMyGamesUseCase: GetInstalledMyGamesUseCase,
  private val installedAppOpener: InstalledAppOpener,
) : ViewModel() {

  private val viewModelState = MutableStateFlow<MyGamesBundleUiState>(MyGamesBundleUiState.Loading)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    retry()
  }

  fun retry() {
    viewModelScope.launch {
      viewModelState.update { MyGamesBundleUiState.Loading }
      getInstalledMyGamesUseCase.getMyGamesAppsList()
        .map(::toMyGamesState)
        .collect { state ->
          viewModelState.update { state }
        }
    }
  }

  private fun toMyGamesState(myGamesAppsList: List<MyGamesApp>) =
    if (myGamesAppsList.isEmpty()) {
      MyGamesBundleUiState.Empty
    } else {
      MyGamesBundleUiState.AppsList(myGamesAppsList)
    }

  fun openApp(packageName: String) {
    installedAppOpener.openInstalledApp(packageName)
  }
}
