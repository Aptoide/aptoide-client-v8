package cm.aptoide.pt.home.bundles.base

import cm.aptoide.pt.dataprovider.model.v7.Event

class AppComingSoonPromotionalBundle(title: String, type: HomeBundle.BundleType, event: Event?,
                                     tag: String, actionItem: ActionItem,
                                     var isRegisteredForNotification: Boolean) :
    ActionBundle(title, type, event, tag, actionItem)