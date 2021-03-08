package cm.aptoide.pt

import cm.aptoide.pt.navigator.ExternalNavigator

class CatappultNavigator(private val externalNavigator: ExternalNavigator) {

  fun navigateToCatappultWebsite() {
    externalNavigator.navigateToExternalWebsite("https://catappult.io/?utm_source=vanilla")
  }
}