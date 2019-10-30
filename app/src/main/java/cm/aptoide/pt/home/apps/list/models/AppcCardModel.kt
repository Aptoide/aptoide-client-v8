package cm.aptoide.pt.home.apps.list.models

import android.widget.ImageView
import android.widget.TextView
import cm.aptoide.pt.R
import cm.aptoide.pt.home.apps.AppClick
import cm.aptoide.pt.home.apps.model.AppcUpdateApp
import cm.aptoide.pt.home.apps.model.InstalledApp
import cm.aptoide.pt.networking.image.ImageLoader
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.fa.epoxysample.bundles.models.base.BaseViewHolder
import rx.subjects.PublishSubject

@EpoxyModelClass(layout = R.layout.apps_appc_promotion_upgrade_app_item)
abstract class AppcCardModel : EpoxyModelWithHolder<AppcCardModel.CardHolder>() {
  @EpoxyAttribute
  var application: AppcUpdateApp? = null

  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var eventSubject: PublishSubject<AppClick>? = null

  override fun bind(holder: CardHolder) {
    application?.let { app ->
      holder.name.text = app.name
      ImageLoader.with(holder.itemView.context).load(app.icon, holder.appIcon)
      holder.version.text = app.version

      holder.actionButton.setOnClickListener {
        eventSubject?.onNext(AppClick(app, AppClick.ClickType.APPC_ACTION_CLICK))
      }
      holder.itemView.setOnClickListener {
        eventSubject?.onNext(AppClick(app, AppClick.ClickType.CARD_CLICK))
      }
      holder.itemView.setOnLongClickListener {
        eventSubject?.onNext(AppClick(app, AppClick.ClickType.CARD_LONG_CLICK))
        true
      }
    }
  }

  class CardHolder : BaseViewHolder() {
    val name by bind<TextView>(R.id.apps_app_name)
    val appIcon by bind<ImageView>(R.id.apps_app_icon)
    val version by bind<TextView>(R.id.apps_secondary_text)
    val actionButton by bind<ImageView>(R.id.apps_action_button)
  }
}