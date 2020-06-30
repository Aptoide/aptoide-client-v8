package cm.aptoide.pt.home.bundles.base

import cm.aptoide.pt.dataprovider.model.v7.Event
import cm.aptoide.pt.view.app.Application

class FeaturedAppcBundle(title: String?, apps: List<Application>?, type: HomeBundle.BundleType?,
                         event: Event?, tag: String?,
                         actionTag: String?, val bonusPercentage: Int) :
    AppBundle(title, apps, type, event, tag, actionTag)