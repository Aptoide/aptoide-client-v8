package com.aptoide.android.aptoidegames.apkfy.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_campaigns.toAptoideMMPCampaign
import cm.aptoide.pt.install_info_mapper.domain.InstallPackageInfoMapper
import cm.aptoide.pt.install_manager.InstallManager
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsUIContext
import com.aptoide.android.aptoidegames.analytics.dto.InstallAction
import com.aptoide.android.aptoidegames.installer.analytics.AnalyticsInstallPackageInfoMapper
import com.aptoide.android.aptoidegames.installer.analytics.InstallAnalytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CompanionAppsSelectionViewModel(
  private val apkfyApp: App,
  private val companionAppsList: List<App>,
  private val installManager: InstallManager,
  private val installPackageInfoMapper: InstallPackageInfoMapper,
  private val installAnalytics: InstallAnalytics
) : ViewModel() {

  private var counter: Int = 0
  private val _selectedIds =
    MutableStateFlow(companionAppsList.filter { it.isAppCoins }.take(1).map { it.packageName }
      .toSet())
  val selectedIds: StateFlow<Set<String>> = _selectedIds

  fun toggleSelection(packageName: String) {
    _selectedIds.update {
      if (packageName in it) {
        counter -= 1
        it - packageName
      } else {
        counter += 1
        it + packageName
      }
    }
  }

  fun install(analyticsContext: AnalyticsUIContext, networkType: String) {
    viewModelScope.launch {
      AnalyticsInstallPackageInfoMapper.currentAnalyticsUIContext =
        analyticsContext.copy(installAction = InstallAction.INSTALL)

      installAnalytics.sendApkfyRobloxExp81InstallClickEvent(counter)
      installAnalytics.sendClickEvent(apkfyApp, analyticsContext, networkType)

      companionAppsList.filter { it.packageName in selectedIds.value }.forEach {
        installManager.getApp(it.packageName).install(installPackageInfoMapper.map(it))

        it.campaigns?.toAptoideMMPCampaign()
          ?.sendClickEvent(bundleTag = analyticsContext.bundleMeta?.tag)
        it.campaigns?.toAptoideMMPCampaign()
          ?.sendDownloadEvent(
            bundleTag = analyticsContext.bundleMeta?.tag,
            searchKeyword = analyticsContext.searchMeta?.searchKeyword,
            currentScreen = analyticsContext.currentScreen,
          )
      }

      installManager.getApp(apkfyApp.packageName).install(installPackageInfoMapper.map(apkfyApp))
      apkfyApp.campaigns?.toAptoideMMPCampaign()
        ?.sendClickEvent(bundleTag = analyticsContext.bundleMeta?.tag)
      apkfyApp.campaigns?.toAptoideMMPCampaign()
        ?.sendDownloadEvent(
          bundleTag = analyticsContext.bundleMeta?.tag,
          searchKeyword = analyticsContext.searchMeta?.searchKeyword,
          currentScreen = analyticsContext.currentScreen,
        )
    }
  }
}

data class CompanionAppsState(
  val selectedPackages: Set<String>,
  val toggleSelection: (String) -> Unit,
  val install: (AnalyticsUIContext, String) -> Unit
)
