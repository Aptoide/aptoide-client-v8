package com.aptoide.android.aptoidegames.apkfy.presentation

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
import cm.aptoide.pt.feature_apkfy.presentation.rememberApkfyApp
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import com.aptoide.android.aptoidegames.apkfy.DownloadPermissionState
import com.aptoide.android.aptoidegames.apkfy.DownloadPermissionStateProbe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val featureFlags: FeatureFlags,
  val downloadPermissionStateProbe: DownloadPermissionStateProbe,
) : ViewModel()

data class ApkfyFeatureFlags(
  val apkfyVariant: String? = null
)

@Composable
fun rememberApkfyState(): ApkfyUiState? = runPreviewable(
  preview = { ApkfyUiState.Default(randomApp) },
  real = {
    val coroutineScope = rememberCoroutineScope()
    val apkfyApp = rememberApkfyApp()
    var apkfyFeatureFlags: ApkfyFeatureFlags? by remember { mutableStateOf(null) }
    val vm = hiltViewModel<InjectionsProvider>()

    val apkfyUiState by remember(apkfyApp, apkfyFeatureFlags) {
      derivedStateOf {
        apkfyApp?.let { app ->
          apkfyFeatureFlags?.let { flags ->
            when (flags.apkfyVariant) {
              "v1" -> ApkfyUiState.VariantA(app)
              "v2" -> ApkfyUiState.VariantB(app)
              "v4" -> ApkfyUiState.VariantC(app)
              "v5" -> ApkfyUiState.VariantD(app)
              else -> ApkfyUiState.Default(app)
            }
          }
        }
      }
    }

    LaunchedEffect(Unit) {
      coroutineScope.launch {
        apkfyFeatureFlags = withTimeoutOrNull(5000) {
          ApkfyFeatureFlags(
            apkfyVariant = vm.featureFlags.getFlagAsString("apkfy_variant"),
          )
        } ?: ApkfyFeatureFlags()
      }
    }

    apkfyUiState
  }
)

@Composable
fun rememberDownloadPermissionState(app: App): DownloadPermissionState? = runPreviewable(
  preview = { DownloadPermissionState.Allowed(app.packageName) },
  real = {
    val injectionsProvider = hiltViewModel<InjectionsProvider>()
    val downloadPermissionStateViewModel: DownloadPermissionStateViewModel = viewModel(
      key = "apkfy.${app.packageName}",
      viewModelStoreOwner = LocalContext.current as AppCompatActivity,
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
