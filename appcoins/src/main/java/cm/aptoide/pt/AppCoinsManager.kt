package cm.aptoide.pt

import cm.aptoide.pt.bonus.BonusAppcModel
import cm.aptoide.pt.bonus.BonusAppcService
import io.reactivex.Single
import kotlinx.coroutines.rx2.rxSingle

open class AppCoinsManager(
  private val bonusAppcService: BonusAppcService
) {

  fun getBonusAppc(): Single<BonusAppcModel> {
    return rxSingle { bonusAppcService.getBonusAppc() }
  }
}