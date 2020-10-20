package cm.aptoide.pt

import cm.aptoide.pt.bonus.BonusAppcModel
import cm.aptoide.pt.bonus.BonusAppcService
import cm.aptoide.pt.donations.Donation
import cm.aptoide.pt.donations.DonationsService
import io.reactivex.Single
import kotlinx.coroutines.rx2.rxSingle

class AppCoinsManager(private val donationsService: DonationsService,
                      private val bonusAppcService: BonusAppcService) {

  fun getBonusAppc(): Single<BonusAppcModel> {
    return rxSingle { bonusAppcService.getBonusAppc() }
  }

  fun getDonationsList(packageName: String): Single<List<Donation>> {
    return rxSingle { donationsService.getDonations(packageName) }
  }
}