package com.aptoide.android.aptoidegames.apkfy.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_apkfy.presentation.rememberApkfyApp
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val featureFlags: FeatureFlags,
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
