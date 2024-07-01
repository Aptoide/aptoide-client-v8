package cm.aptoide.pt.feature_mmp.apkfy.repository

import cm.aptoide.pt.feature_mmp.apkfy.domain.ApkfyModel

interface ApkfyRepository {

  suspend fun getApkfy(): ApkfyModel
}
