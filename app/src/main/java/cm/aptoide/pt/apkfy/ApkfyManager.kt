package cm.aptoide.pt.apkfy

class ApkfyManager(
  private val apkfyService: ApkfyService
) {

  suspend fun getApkfy(): ApkfyModel {
    return apkfyService.getApkfy()
  }
}
