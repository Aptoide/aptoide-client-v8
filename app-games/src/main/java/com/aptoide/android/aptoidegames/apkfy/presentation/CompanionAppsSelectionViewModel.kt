package com.aptoide.android.aptoidegames.apkfy.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.download_view.presentation.InstalledAppOpener
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_campaigns.UTMInfo
import cm.aptoide.pt.feature_campaigns.toAptoideMMPCampaign
import cm.aptoide.pt.install_info_mapper.domain.InstallPackageInfoMapper
import cm.aptoide.pt.install_manager.InstallManager
import cm.aptoide.pt.install_manager.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CompanionAppsSelectionViewModel(
  private val apkfyApp: App,
  private val companionAppsList: List<App>,
  private val installManager: InstallManager,
  private val installPackageInfoMapper: InstallPackageInfoMapper,
  private val installedAppOpener: InstalledAppOpener
) : ViewModel() {

  private val _selectedIds =
    MutableStateFlow(
      companionAppsList.filter { it.isAppCoins }
        .take(1).map { it.packageName }
        .plus(apkfyApp.packageName)
        .toSet()
    )
  val selectedIds: StateFlow<Set<String>> = _selectedIds

  fun toggleSelection(packageName: String) {
    _selectedIds.update {
      if (packageName in it) {
        it - packageName
      } else {
        it + packageName
      }
    }
  }

  fun install(
    utmInfo: UTMInfo,
    autoOpenFinal: Boolean
  ) {
    viewModelScope.launch {
      val selectedCompanionApps = companionAppsList.filter { it.packageName in selectedIds.value }
      val installedPackages = installManager.installedApps.map { it.packageName }.toSet()
      val appsToInstall = if (apkfyApp.packageName in selectedIds.value) {
        selectedCompanionApps + apkfyApp
      } else {
        selectedCompanionApps
      }.filter { it.packageName !in installedPackages }

      var firstCompanionTask: Task? = null
      appsToInstall.forEachIndexed { index, app ->
        val task = installManager.getApp(app.packageName)
          .install(installPackageInfoMapper.map(app))
        if (index == 0) firstCompanionTask = task

        app.campaigns?.toAptoideMMPCampaign()
          ?.sendClickEvent(utmInfo)
        app.campaigns?.toAptoideMMPCampaign()
          ?.sendDownloadEvent(
            utmInfo
          )
      }

      if (autoOpenFinal) {
        firstCompanionTask?.let { task ->
          task.stateAndProgress
            .last()
            .let { state ->
              if (state is Task.State.Completed) {
                installedAppOpener.openInstalledApp(appsToInstall.first().packageName)
              }
            }
        }
      }
    }
  }
}

data class CompanionAppsState(
  val selectedPackages: Set<String>,
  val toggleSelection: (String) -> Unit,
  val install: (UTMInfo, Boolean) -> Unit
)
