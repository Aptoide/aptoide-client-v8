package cm.aptoide.pt.feature_apkfy.repository

import cm.aptoide.pt.feature_apkfy.domain.ApkfyModel

interface ApkfyRepository {

  suspend fun getApkfy(): ApkfyModel
}
