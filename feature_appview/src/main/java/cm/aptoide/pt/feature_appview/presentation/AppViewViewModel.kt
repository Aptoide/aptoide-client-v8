package cm.aptoide.pt.feature_appview.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_appview.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AppViewViewModel @Inject constructor(
  private val getAppInfoUseCase: GetAppInfoUseCase,
  private val getOtherVersionsUseCase: GetAppOtherVersionsUseCase,
  getReviewsUseCase: GetReviewsUseCase,
  setAppReviewUseCase: SetAppReviewUseCase,
  private val getSimilarAppsUseCase: GetSimilarAppsUseCase,
  private val getAppcSimilarAppsUseCase: GetAppcSimilarAppsUseCase,
  reportAppUseCase: ReportAppUseCase,
  shareAppUseCase: ShareAppUseCase,
  private val savedStateHandle: SavedStateHandle,
  private val tabsList: TabsListProvider,
) : ViewModel() {

  private val packageName: String? = savedStateHandle.get("packageName")
  private val viewModelState =
    MutableStateFlow(AppViewViewModelState(tabsList = tabsList.getTabsList()))

  val uiState = viewModelState.map { it.toUiState() }
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value.toUiState()
    )

  init {
    viewModelScope.launch {
      viewModelState.update { it.copy(type = AppViewUiStateType.LOADING) }
      packageName?.let { it ->
        getAppInfoUseCase.getAppInfo(it)
          .catch { e ->
            Timber.w(e)
            viewModelState.update {
              it.copy(
                type = when (e) {
                  is IOException -> AppViewUiStateType.NO_CONNECTION
                  else -> AppViewUiStateType.ERROR
                }
              )
            }
          }
          .collect { app ->
            viewModelState.update { state ->
              app.campaigns?.sendImpressionEvent()
              state.copy(app = app, type = AppViewUiStateType.IDLE)
            }
          }
      }
    }
  }

  fun onSelectAppViewTab(appViewTab: Pair<AppViewTab, Int>, packageName: String?) {
    if (appViewTab.first == AppViewTab.VERSIONS) {
      loadOtherVersions(packageName)
    }
    viewModelState.update { it.copy(selectedTab = appViewTab) }
  }

  private fun loadOtherVersions(packageName: String?) {
    packageName?.let {
      viewModelScope.launch {
        getOtherVersionsUseCase.getOtherVersions(it)
          .catch { e -> Timber.w(e) }
          .collect { apps ->
            viewModelState.update { it.copy(otherVersionsList = apps) }
          }
      }
    }
  }

  fun loadRecommendedApps(packageName: String) {
/*    viewModelScope.launch {
      getSimilarAppsUseCase.getSimilarApps(packageName).collect { similarAppsResult ->
        viewModelState.update {
          when (similarAppsResult) {
            is SimilarAppsResult.Success -> {
              it.copy(
                similarAppsList = similarAppsResult.similarApps,
              )
            }
            is SimilarAppsResult.Error -> {
              it.copy()
            }
          }
        }
      }

      getAppcSimilarAppsUseCase.getAppcSimilarApps(packageName).collect { similarAppcAppsResult ->
        viewModelState.update {
          when (similarAppcAppsResult) {
            is SimilarAppsResult.Success -> {
              it.copy(
                similarAppcAppsList = similarAppcAppsResult.similarApps,
              )
            }
            is SimilarAppsResult.Error -> {
              it.copy()
            }
          }
        }
      }


    }*/
  }

}


private data class AppViewViewModelState(
  val app: App? = null,
  val type: AppViewUiStateType = AppViewUiStateType.IDLE,
  val selectedTab: Pair<AppViewTab, Int> = Pair(AppViewTab.DETAILS, 0),
  val tabsList: List<Pair<AppViewTab, Int>> = emptyList(),
  val similarAppsList: List<App> = emptyList(),
  val similarAppcAppsList: List<App> = emptyList(),
  val otherVersionsList: List<App> = emptyList(),
) {

  fun toUiState(): AppViewUiState =
    AppViewUiState(
      app = app,
      type = type,
      selectedTab = selectedTab,
      tabsList = tabsList,
      similarAppsList = similarAppsList,
      similarAppcAppsList = similarAppcAppsList,
      otherVersionsList = otherVersionsList,
    )
}
