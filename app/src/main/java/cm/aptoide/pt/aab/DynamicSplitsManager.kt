package cm.aptoide.pt.aab

import io.reactivex.Single
import kotlinx.coroutines.rx2.rxSingle

class DynamicSplitsManager(private val dynamicSplitsService: DynamicSplitsService) {

  fun getAppSplitsByMd5(apkMd5Sum: String): Single<DynamicSplitsModel> {
    return rxSingle { dynamicSplitsService.getDynamicSplitsByMd5(apkMd5Sum) }
  }
}