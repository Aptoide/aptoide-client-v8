package cm.aptoide.pt.home.bundles.apps

import cm.aptoide.pt.view.app.Application

class RewardApp(appName: String, appIcon: String, ratingAverage: Float, downloadsNumber: Int,
                packageName: String, appId: Long, tag: String, hasBilling: Boolean,
                val clickUrl: String? = "", val downloadUrl: String? = "", val reward: Reward?,
                val featureGraphic: String? = "") :
    Application(appName, appIcon, ratingAverage, downloadsNumber, packageName, appId, tag,
        hasBilling) {

  data class Reward(val appc: Double, val fiat: Fiat)
  data class Fiat(var amount: Double = -1.0, var currency: String = "", var symbol: String = "")

}