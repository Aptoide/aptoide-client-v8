package cm.aptoide.pt.feature_apkfy.presentation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_apkfy.domain.ApkfyFilter
import cm.aptoide.pt.feature_apkfy.domain.ApkfyManager
import cm.aptoide.pt.feature_apkfy.domain.ApkfyModel
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.domain.AppMetaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ApkfyData(
  val app: App,
  val utmSource: String?,
  val utmMedium: String?,
  val utmCampaign: String?,
  val utmContent: String?,
  val utmTerm: String?,
)

private var cachedApkfyData: ApkfyData? = null

@HiltViewModel
class ApkfyViewModel @Inject constructor(
  @ApplicationContext private val context: Context,
  private val apkfyManager: ApkfyManager,
  private val appMetaUseCase: AppMetaUseCase,
  private val apkfyFilter: ApkfyFilter
) : ViewModel() {

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
              val app = appMetaUseCase.getMetaInfo(source = apkfyModel.asSource())
              cachedApkfyData = apkfyModel.toApkfyData(app)
              viewModelState.update { cachedApkfyData }
            }
        } catch (e: Throwable) {
          e.printStackTrace()
        }
      } else {
        viewModelState.update { cachedApkfyData }
      }
    }
  }

  private fun ApkfyModel.toApkfyData(app: App) = ApkfyData(
    app = app,
    utmSource = utmSource,
    utmMedium = utmMedium,
    utmCampaign = utmCampaign,
    utmContent = utmContent,
    utmTerm = utmTerm,
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
