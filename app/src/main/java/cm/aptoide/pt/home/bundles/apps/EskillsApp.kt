package cm.aptoide.pt.home.bundles.apps

import cm.aptoide.pt.view.app.Application

class EskillsApp(appName: String, appIcon: String, ratingAverage: Float, downloadsNumber: Int,
                 packageName: String, appId: Long, tag: String, hasBilling: Boolean,
                 val featureGraphic: String? = "") :
    Application(appName, appIcon, ratingAverage, downloadsNumber, packageName, appId, tag,
        hasBilling) {
}