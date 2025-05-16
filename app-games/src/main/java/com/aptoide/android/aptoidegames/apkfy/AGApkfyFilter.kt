package com.aptoide.android.aptoidegames.apkfy

import cm.aptoide.pt.feature_apkfy.domain.ApkfyFilter
import cm.aptoide.pt.feature_apkfy.domain.ApkfyModel

class AGApkfyFilter : ApkfyFilter {
  override fun filter(apkfyModel: ApkfyModel) =
    apkfyModel.takeIf { it.utmSource != "AG" && it.utmSource != "AG_dev" }
}
