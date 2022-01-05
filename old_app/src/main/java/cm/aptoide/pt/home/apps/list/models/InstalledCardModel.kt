package cm.aptoide.pt.home.apps.list.models

import android.widget.ImageView
import android.widget.TextView
import cm.aptoide.pt.R
import cm.aptoide.pt.home.apps.model.InstalledApp
import cm.aptoide.pt.networking.image.ImageLoader
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.fa.epoxysample.bundles.models.base.BaseViewHolder

@EpoxyModelClass(layout = R.layout.apps_installed_app_item)
abstract class InstalledCardModel : EpoxyModelWithHolder<InstalledCardModel.CardHolder>() {
  @EpoxyAttribute
  var application: InstalledApp? = null

  override fun bind(holder: CardHolder) {
    application?.let { app ->
      holder.name.text = app.appName
      ImageLoader.with(holder.itemView.context).load(app.icon, holder.appIcon)
      holder.version.text = app.version
    }
  }

  class CardHolder : BaseViewHolder() {
    val name by bind<TextView>(R.id.apps_installed_app_name)
    val appIcon by bind<ImageView>(R.id.apps_installed_icon)
    val version by bind<TextView>(R.id.apps_installed_app_version)
  }
}