package cm.aptoide.pt.app.appc

import rx.Single

interface BonusAppcService {
  fun getBonusAppc(): Single<BonusAppcModel>
}