package cm.aptoide.pt.feature_appview.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_appview.domain.AppInfoUseCase
import cm.aptoide.pt.feature_appview.domain.AppVersionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

class AppViewModel constructor(
  private val appInfoUseCase: AppInfoUseCase,
  private val getOtherVersionsUseCase: AppVersionsUseCase,
  private val packageName: String,
  private val adListId: String?,
  tabsList: TabsListProvider,
) : ViewModel() {

  private val viewModelState =
    MutableStateFlow(AppViewViewModelState(tabsList = tabsList.getTabsList()))

  val uiState = viewModelState.map { it.toUiState() }
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value.toUiState()
    )

  init {
    reload()
  }

  fun reload() {
    viewModelScope.launch {
      viewModelState.update { it.copy(type = AppUiStateType.LOADING) }
      packageName.let { it ->
        appInfoUseCase.getAppInfo(it).map { app ->
          app.campaigns?.adListId = adListId
          app
        }
          .catch { e ->
            Timber.w(e)
            viewModelState.update {
              it.copy(
                type = when (e) {
                  is IOException -> AppUiStateType.NO_CONNECTION
                  else -> AppUiStateType.ERROR
                }
              )
            }
          }
          .collect { app ->
            viewModelState.update { state ->
              app.campaigns?.sendImpressionEvent()
              state.copy(app = app, type = AppUiStateType.IDLE)
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
        getOtherVersionsUseCase.getAppVersions(it)
          .catch { e -> Timber.w(e) }
          .collect { apps ->
            viewModelState.update { it.copy(otherVersionsList = apps) }
          }
      }
    }
  }
}


private data class AppViewViewModelState(
  val app: App? = null,
  val type: AppUiStateType = AppUiStateType.IDLE,
  val selectedTab: Pair<AppViewTab, Int> = Pair(AppViewTab.DETAILS, 0),
  val tabsList: List<Pair<AppViewTab, Int>> = emptyList(),
  val similarAppsList: List<App> = emptyList(),
  val similarAppcAppsList: List<App> = emptyList(),
  val otherVersionsList: List<App> = emptyList(),
) {

  fun toUiState(): AppUiState =
    AppUiState(
      app = app,
      type = type,
      selectedTab = selectedTab,
      tabsList = tabsList,
      similarAppsList = similarAppsList,
      similarAppcAppsList = similarAppcAppsList,
      otherVersionsList = otherVersionsList,
    )
}
