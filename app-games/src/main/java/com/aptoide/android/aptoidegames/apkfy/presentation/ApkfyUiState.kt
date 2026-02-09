package com.aptoide.android.aptoidegames.apkfy.presentation

import cm.aptoide.pt.feature_apkfy.presentation.ApkfyData

sealed class ApkfyUiState(val data: ApkfyData) {
  class Default(data: ApkfyData) : ApkfyUiState(data)
  class Baseline(data: ApkfyData) : ApkfyUiState(data)
  class RobloxCompanionAppsVariant(data: ApkfyData, val autoOpenDefault: Boolean) : ApkfyUiState(data)
}
