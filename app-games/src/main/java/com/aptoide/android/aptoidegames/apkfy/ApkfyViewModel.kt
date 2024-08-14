package com.aptoide.android.aptoidegames.apkfy

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.domain.AppMetaUseCase
import cm.aptoide.pt.feature_apps.presentation.toAppIdParam
import cm.aptoide.pt.feature_apps.presentation.toPackageNameParam
import cm.aptoide.pt.feature_mmp.apkfy.domain.ApkfyManager
import cm.aptoide.pt.feature_mmp.apkfy.domain.ApkfyModel
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApkfyViewModel @Inject constructor(
  @ApplicationContext private val context: Context,
  private val apkfyManager: ApkfyManager,
  private val appMetaUseCase: AppMetaUseCase,
  private val biAnalytics: BIAnalytics,
) : ViewModel() {

  companion object {
    private const val UTM_PROPERTY_NO_APKFY = "NO_APKFY"
    private const val UTM_PROPERTY_APKFY_WITHOUT_UTMS = "APKFY_BUT_NO_UTM"
  }

  private val viewModelState = MutableStateFlow<App?>(value = null)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      try {
        apkfyManager.getApkfy()
          ?.also { setApkfyUTMProperties(it) }
          ?.takeIf { it.packageName != context.packageName }
          ?.run {
            (appId?.toAppIdParam() ?: packageName?.toPackageNameParam())
              ?.let { source ->
                var app = appMetaUseCase.getMetaInfoBySource(
                  source = source,
                  useStoreName = false
                )

                if (oemId != null) {
                  val oemIdQuery = "?oemid=${oemId}"
                  app = app.copy(
                    file = app.file.run {
                      copy(
                        path = path + oemIdQuery,
                        path_alt = path_alt + oemIdQuery
                      )
                    }
                  )
                }

                viewModelState.update { app }
              }
          }
      } catch (e: Throwable) {
        e.printStackTrace()
      }
    }
  }

  fun setApkfyUTMProperties(apkfyModel: ApkfyModel) {
    apkfyModel.run {
      if (hasUTMs()) {
        biAnalytics.setUTMProperties(
          utmSource = utmSource,
          utmMedium = utmMedium,
          utmCampaign = utmCampaign,
          utmTerm = utmTerm,
          utmContent = utmContent
        )
      } else if (packageName == null && oemId == null) {
        //Safe to assume there are no utms, so no need to check
        biAnalytics.setUTMProperties(
          utmSource = UTM_PROPERTY_NO_APKFY,
          utmMedium = UTM_PROPERTY_NO_APKFY,
          utmCampaign = UTM_PROPERTY_NO_APKFY,
          utmTerm = UTM_PROPERTY_NO_APKFY,
          utmContent = UTM_PROPERTY_NO_APKFY
        )
      } else {
        biAnalytics.setUTMProperties(
          utmSource = UTM_PROPERTY_APKFY_WITHOUT_UTMS,
          utmMedium = UTM_PROPERTY_APKFY_WITHOUT_UTMS,
          utmCampaign = UTM_PROPERTY_APKFY_WITHOUT_UTMS,
          utmTerm = UTM_PROPERTY_APKFY_WITHOUT_UTMS,
          utmContent = UTM_PROPERTY_APKFY_WITHOUT_UTMS
        )
      }
    }
  }
}

@Composable
fun rememberApkfyApp() = runPreviewable(
  preview = { null },
  real = {
    val vm = hiltViewModel<ApkfyViewModel>()
    val app by vm.uiState.collectAsState()
    app
  }
)
