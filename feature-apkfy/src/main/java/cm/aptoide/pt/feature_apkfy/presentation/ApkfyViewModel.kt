package cm.aptoide.pt.feature_apkfy.presentation

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_apkfy.domain.ApkfyAppResolver
import cm.aptoide.pt.feature_apkfy.domain.ApkfyCatalogFallback
import cm.aptoide.pt.feature_apkfy.domain.ApkfyFilter
import cm.aptoide.pt.feature_apkfy.domain.ApkfyManager
import cm.aptoide.pt.feature_apkfy.domain.ApkfyModel
import cm.aptoide.pt.feature_apkfy.domain.ApkfyResolution
import cm.aptoide.pt.feature_apkfy.domain.ApkfyResolutionReporter
import cm.aptoide.pt.feature_apkfy.domain.UnavailableReason
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.aptoide_network.di.V7Backend
import cm.aptoide.pt.feature_apps.domain.AppMetaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Optional
import javax.inject.Inject

data class ApkfyData(
  val app: App,
  val utmSource: String?,
  val utmMedium: String?,
  val utmCampaign: String?,
  val utmContent: String?,
  val utmTerm: String?,
  /** Set when the title is shown but cannot be installed. */
  val unavailableReason: UnavailableReason? = null,
)

private var cachedApkfyData: ApkfyData? = null

@HiltViewModel
class ApkfyViewModel @Inject constructor(
  @ApplicationContext private val context: Context,
  private val apkfyManager: ApkfyManager,
  // The v7 app whatever the build reads its catalog from: apkfy's titles stay on v7
  @V7Backend appMetaUseCase: AppMetaUseCase,
  private val apkfyFilter: ApkfyFilter,
  // Only the builds that read a second catalog bind one
  catalogFallback: Optional<ApkfyCatalogFallback>,
  private val resolutionReporter: ApkfyResolutionReporter,
) : ViewModel() {

  private val resolver = ApkfyAppResolver(
    v7 = appMetaUseCase::getMetaInfo,
    fallback = catalogFallback.orElse(null),
    deviceSdk = { Build.VERSION.SDK_INT },
  )

  private val viewModelState = MutableStateFlow<ApkfyData?>(value = null)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      if (cachedApkfyData == null) {
        try {
          apkfyManager.getApkfy()
            ?.takeIf { it.packageName != context.packageName }
            ?.takeIf { it.appId != null || it.packageName != null }
            ?.let(apkfyFilter::filter)
            ?.let { apkfyModel ->
              val resolution = resolver.resolve(apkfyModel)
              resolutionReporter.report(apkfyModel.packageName, resolution)
              // A title not found, or whose lookup failed, shows nothing, as it always has
              if (resolution is ApkfyResolution.Resolved) {
                cachedApkfyData = apkfyModel.toApkfyData(resolution)
                viewModelState.update { cachedApkfyData }
              }
            }
        } catch (e: Throwable) {
          e.printStackTrace()
        }
      } else {
        viewModelState.update { cachedApkfyData }
      }
    }
  }

  private fun ApkfyModel.toApkfyData(resolution: ApkfyResolution.Resolved) = ApkfyData(
    app = resolution.app,
    utmSource = utmSource,
    utmMedium = utmMedium,
    utmCampaign = utmCampaign,
    utmContent = utmContent,
    utmTerm = utmTerm,
    unavailableReason = resolution.unavailableReason,
  )
}

@Composable
fun rememberApkfyData() = runPreviewable(
  preview = { null },
  real = {
    val vm = hiltViewModel<ApkfyViewModel>()
    val apkfyData by vm.uiState.collectAsState()
    apkfyData
  }
)
