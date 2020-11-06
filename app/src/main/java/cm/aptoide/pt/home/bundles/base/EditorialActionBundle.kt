package cm.aptoide.pt.home.bundles.base

import cm.aptoide.pt.bonus.BonusAppcModel
import cm.aptoide.pt.dataprovider.model.v7.Event

class EditorialActionBundle(title: String, type: HomeBundle.BundleType, event: Event?,
                            tag: String, actionItem: ActionItem,
                            val bonusAppcModel: BonusAppcModel) :
    ActionBundle(title, type, event, tag, actionItem) {


}