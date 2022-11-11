package cm.aptoide.pt.feature_appview.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.DetailedApp
import cm.aptoide.pt.feature_appview.domain.model.RelatedCard
import cm.aptoide.pt.feature_appview.domain.repository.AppViewResult
import cm.aptoide.pt.feature_appview.domain.repository.OtherVersionsResult
import cm.aptoide.pt.feature_appview.domain.repository.RelatedContentResult
import cm.aptoide.pt.feature_appview.domain.usecase.*
import cm.aptoide.pt.feature_campaigns.CampaignsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewViewModel @Inject constructor(
  private val getAppInfoUseCase: GetAppInfoUseCase,
  private val getOtherVersionsUseCase: GetAppOtherVersionsUseCase,
  private val getRelatedContentUseCase: GetRelatedContentUseCase,
  getReviewsUseCase: GetReviewsUseCase,
  setAppReviewUseCase: SetAppReviewUseCase,
  private val getSimilarAppsUseCase: GetSimilarAppsUseCase,
  private val getAppcSimilarAppsUseCase: GetAppcSimilarAppsUseCase,
  reportAppUseCase: ReportAppUseCase,
  shareAppUseCase: ShareAppUseCase,
  private val campaignsUseCase: CampaignsUseCase,
  private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

  private val packageName: String? = savedStateHandle.get("packageName")
  private val viewModelState = MutableStateFlow(AppViewViewModelState())

  val uiState = viewModelState.map { it.toUiState() }
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value.toUiState()
    )

  init {
    viewModelScope.launch {
      packageName?.let {
        getAppInfoUseCase.getAppInfo(it)
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
        campaignsUseCase.getCampaign(it)?.sendImpressionEvent()
      }
    }
  }

  fun onSelectAppViewTab(appViewTab: AppViewTab, packageName: String?) {
    if (appViewTab == AppViewTab.VERSIONS) {
      loadOtherVersions(packageName)
    } else if (appViewTab == AppViewTab.RELATED) {
      loadRelatedContent(packageName)
    }
    viewModelState.update { it.copy(selectedTab = appViewTab) }
  }

  private fun loadRelatedContent(packageName: String?) {
    packageName?.let {
      viewModelScope.launch {
        getRelatedContentUseCase.getRelatedContent(it)
          .catch { throwable -> throwable.printStackTrace() }
          .collect { relatedContentResult ->
            viewModelState.update {
              when (relatedContentResult) {
                is RelatedContentResult.Success -> {
                  it.copy(relatedContent = relatedContentResult.relatedContent)
                }
                is RelatedContentResult.Error -> {
                  it.copy()
                }
              }
            }
          }
      }
    }
  }

  private fun loadOtherVersions(packageName: String?) {
    packageName?.let {
      viewModelScope.launch {
        getOtherVersionsUseCase.getOtherVersions(it)
          .catch { throwable -> throwable.printStackTrace() }
          .collect { otherVersionsResult ->
            viewModelState.update {
              when (otherVersionsResult) {
                is OtherVersionsResult.Success -> {
                  it.copy(otherVersionsList = otherVersionsResult.otherVersionsList)
                }
                is OtherVersionsResult.Error -> {
                  otherVersionsResult.error.printStackTrace()
                  it.copy()
                }
              }
            }
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
  val app: DetailedApp? = null,
  val isLoading: Boolean = false,
  val selectedTab: AppViewTab = AppViewTab.DETAILS,
  val tabsList: List<AppViewTab> = listOf(
    AppViewTab.DETAILS,
    AppViewTab.REVIEWS,
    AppViewTab.RELATED,
    AppViewTab.VERSIONS,
    AppViewTab.INFO
  ),
  val similarAppsList: List<App> = emptyList(),
  val similarAppcAppsList: List<App> = emptyList(),
  val otherVersionsList: List<App> = emptyList(),
  val relatedContent: List<RelatedCard> = emptyList(),
) {

  fun toUiState(): AppViewUiState =
    AppViewUiState(
      app = app,
      isLoading = isLoading,
      selectedTab = selectedTab,
      tabsList = tabsList,
      similarAppsList = similarAppsList,
      similarAppcAppsList = similarAppcAppsList,
      otherVersionsList = otherVersionsList,
      relatedContent = relatedContent
    )
}
