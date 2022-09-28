package cm.aptoide.pt.app.appsflyer

import rx.Single

class AppsFlyerManager(private val appsFlyerRepository: AppsFlyerRepository) {

  fun registerImpression(): Single<Boolean> {
    return appsFlyerRepository.registerImpression()
  }
}