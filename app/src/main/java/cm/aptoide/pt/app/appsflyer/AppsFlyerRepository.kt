package cm.aptoide.pt.app.appsflyer

import rx.Single

class AppsFlyerRepository(private val appsFlyerService: AppsFlyerService) {

  fun registerImpression(): Single<Boolean> {
    return appsFlyerService.registerImpression().map { response ->
      response.isSuccessful
    }
  }

}