package com.aptoide.android.aptoidegames.apkfy.presentation

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_apkfy.presentation.ApkfyData
import cm.aptoide.pt.feature_apkfy.presentation.rememberApkfyData
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import cm.aptoide.pt.install_info_mapper.domain.InstallPackageInfoMapper
import cm.aptoide.pt.install_manager.InstallManager
import com.aptoide.android.aptoidegames.apkfy.DownloadPermissionState
import com.aptoide.android.aptoidegames.apkfy.DownloadPermissionStateProbe
import com.aptoide.android.aptoidegames.apkfy.isRoblox
import com.aptoide.android.aptoidegames.installer.analytics.InstallAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val featureFlags: FeatureFlags,
  val downloadPermissionStateProbe: DownloadPermissionStateProbe,
  val installManager: InstallManager,
  val installPackageInfoMapper: InstallPackageInfoMapper,
  val installAnalytics: InstallAnalytics
) : ViewModel()

data class ApkfyFeatureFlags(
  val apkfyVariant: String? = null,
)

val previewApkfyData = ApkfyData(
  app = randomApp,
  utmSource = null,
  utmMedium = null,
  utmCampaign = null,
  utmContent = null,
  utmTerm = null,
)

@Composable
fun rememberApkfyState(): ApkfyUiState? = runPreviewable(
  preview = { ApkfyUiState.Default(previewApkfyData) },
  real = {
    val coroutineScope = rememberCoroutineScope()
    val apkfyData = rememberApkfyData()
    var apkfyFeatureFlags: ApkfyFeatureFlags? by remember { mutableStateOf(null) }
    val vm = hiltViewModel<InjectionsProvider>()

    val apkfyUiState by remember(apkfyData, apkfyFeatureFlags) {
      derivedStateOf {
        apkfyData?.let { data ->
          apkfyFeatureFlags?.let { flags ->
            if (data.app.isRoblox()) {
              when (flags.apkfyVariant) {
                "baseline" -> ApkfyUiState.Baseline(data)
                "roblox_multi_install" -> ApkfyUiState.RobloxCompanionAppsVariant(data)
                else -> ApkfyUiState.Default(data)
              }
            } else {
              ApkfyUiState.Baseline(data)
            }
          }
        }
      }
    }

    LaunchedEffect(Unit) {
      coroutineScope.launch {
        apkfyFeatureFlags = withTimeoutOrNull(5000) {
          val flag =
            vm.featureFlags.getFlagAsString("exp81_apkfy_variant")
          ApkfyFeatureFlags(
            apkfyVariant = flag
          )
        } ?: ApkfyFeatureFlags()
      }
    }

    apkfyUiState
  }
)

@SuppressLint("ContextCastToActivity")
@Suppress("unused")
@Composable
fun rememberDownloadPermissionState(app: App): DownloadPermissionState? = runPreviewable(
  preview = { DownloadPermissionState.Allowed(app.packageName) },
  real = {
    val injectionsProvider = hiltViewModel<InjectionsProvider>()
    val downloadPermissionStateViewModel: DownloadPermissionStateViewModel = viewModel(
      key = "apkfy.${app.packageName}",
      viewModelStoreOwner = LocalActivity.current as AppCompatActivity,
      factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          @Suppress("UNCHECKED_CAST")
          return DownloadPermissionStateViewModel(
            app = app,
            probe = injectionsProvider.downloadPermissionStateProbe,
          ) as T
        }
      }
    )
    val state by downloadPermissionStateViewModel.uiState.collectAsState()

    state
  }
)

@SuppressLint("ContextCastToActivity")
@Composable
fun rememberCompanionAppsSelection(apkfyApp: App, appList: List<App>): CompanionAppsState =
  runPreviewable(
    preview = { CompanionAppsState(setOf("cm.aptoide.pt"), {}, { _, _ -> }) },
    real = {
      val injectionsProvider = hiltViewModel<InjectionsProvider>()
      val companionAppsSelectionViewModel: CompanionAppsSelectionViewModel = viewModel(
        key = "companionApps.${appList.hashCode()}",
        viewModelStoreOwner = LocalContext.current as AppCompatActivity,
        factory = object : ViewModelProvider.Factory {
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return CompanionAppsSelectionViewModel(
              apkfyApp = apkfyApp,
              companionAppsList = appList,
              installManager = injectionsProvider.installManager,
              installPackageInfoMapper = injectionsProvider.installPackageInfoMapper,
              installAnalytics = injectionsProvider.installAnalytics
            ) as T
          }
        }
      )
      val state by companionAppsSelectionViewModel.selectedIds.collectAsState()
      CompanionAppsState(
        selectedPackages = state,
        toggleSelection = companionAppsSelectionViewModel::toggleSelection,
        install = companionAppsSelectionViewModel::install
      )
    }
  )
