package cm.aptoide.pt.feature_appview.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_appview.domain.repository.AppViewResult
import cm.aptoide.pt.feature_appview.domain.repository.SimilarAppsResult
import cm.aptoide.pt.feature_appview.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewViewModel @Inject constructor(
  private val getAppInfoUseCase: GetAppInfoUseCase,
  getAppOtherVersionsUseCase: GetAppOtherVersionsUseCase,
  getRelatedContentUseCase: GetRelatedContentUseCase,
  getReviewsUseCase: GetReviewsUseCase,
  setAppReviewUseCase: SetAppReviewUseCase,
  private val getSimilarAppsUseCase: GetSimilarAppsUseCase,
  reportAppUseCase: ReportAppUseCase,
  shareAppUseCase: ShareAppUseCase
) : ViewModel() {


  private val viewModelState = MutableStateFlow(AppViewViewModelState())

  val uiState = viewModelState.map { it.toUiState() }
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value.toUiState()
    )

  init {
    viewModelScope.launch {
      getAppInfoUseCase.getAppInfo("com.mobile.legends")
        .catch { throwable -> throwable.printStackTrace() }
        .collect { appViewResult ->
          viewModelState.update {
            when (appViewResult) {
              is AppViewResult.Success -> {
                it.copy(appViewResult.data, false)
              }
              is AppViewResult.Error -> {
                appViewResult.error.printStackTrace()
                it.copy()
              }
            }
          }
        }
    }
  }

  fun onSelectAppViewTab(appViewTab: AppViewTab) {
    viewModelState.update { it.copy(selectedTab = appViewTab) }
  }

  fun loadRecommendedApps(packageName: String) {
    viewModelScope.launch {
      getSimilarAppsUseCase.getSimilarApps(packageName).collect { similarAppsResult ->
        viewModelState.update {
          when (similarAppsResult) {
            is SimilarAppsResult.Success -> {
              it.copy(
                similarAppsList = similarAppsResult.similarApps,
                similarAppcAppsList = similarAppsResult.appcSimilarApps
              )
            }
            is SimilarAppsResult.Error -> {
              it.copy()
            }
          }
        }
      }
    }
  }

}


private data class AppViewViewModelState(
  val app: App? = null,
  val isLoading: Boolean = false,
  val selectedTab: AppViewTab = AppViewTab.DETAILS,
  val tabsList: List<AppViewTab> = listOf(
    AppViewTab.DETAILS,
    AppViewTab.REVIEWS,
    AppViewTab.NFT,
    AppViewTab.RELATED,
    AppViewTab.VERSIONS,
    AppViewTab.INFO
  ), val similarAppsList: List<App> = emptyList(), val similarAppcAppsList: List<App> = emptyList()
) {

  fun toUiState(): AppViewUiState =
    AppViewUiState(app, isLoading, selectedTab, tabsList, similarAppsList, similarAppcAppsList)
}