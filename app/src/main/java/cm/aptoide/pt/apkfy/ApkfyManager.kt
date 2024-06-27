package cm.aptoide.pt.apkfy

import io.reactivex.Single
import kotlinx.coroutines.rx2.rxSingle

class ApkfyManager(
  private val apkfyService: ApkfyService
) {

  fun getApkfy(): Single<ApkfyModel> {
    return rxSingle { apkfyService.getApkfy() }
  }
}
