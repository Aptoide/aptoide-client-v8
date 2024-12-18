package com.aptoide.android.aptoidegames.apkfy.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_apps.data.App
import com.aptoide.android.aptoidegames.apkfy.DownloadPermissionStateProbe
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn

@Suppress("OPT_IN_USAGE")
class DownloadPermissionStateViewModel(
  private val app: App,
  probe: DownloadPermissionStateProbe,
) : ViewModel() {

  val uiState = probe.permissionsResult
    .filter { it?.packageName == app.packageName }
    .stateIn(
      viewModelScope,
      SharingStarted.Companion.Eagerly,
      null
    )
}
