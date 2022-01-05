package cm.aptoide.pt.download.view.outofspace

import android.widget.ImageView
import android.widget.TextView
import cm.aptoide.pt.R
import cm.aptoide.pt.networking.image.ImageLoader
import cm.aptoide.pt.utils.AptoideUtils
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.fa.epoxysample.bundles.models.base.BaseViewHolder
import rx.subjects.PublishSubject

@EpoxyModelClass(layout = R.layout.out_of_space_installed_app_card)
abstract class InstalledAppCardModel : EpoxyModelWithHolder<InstalledAppCardModel.CardHolder>() {

  @EpoxyAttribute
  var application: InstalledApp? = null

  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var eventSubject: PublishSubject<String>? = null


  override fun bind(holder: CardHolder) {
    application?.let { app ->
      holder.name.text = app.appName
      ImageLoader.with(holder.itemView.context).load(app.icon, holder.appIcon)
      holder.appSize.text = AptoideUtils.StringU.formatBytes(app.size, false)
      handleUninstallClick(holder, app)
    }
  }

  private fun handleUninstallClick(holder: CardHolder, app: InstalledApp) {
    holder.uninstallButton.setOnClickListener { eventSubject?.onNext(app.packageName) }
  }


  class CardHolder : BaseViewHolder() {
    val appIcon by bind<ImageView>(R.id.app_icon)
    val name by bind<TextView>(R.id.app_name)
    val appSize by bind<TextView>(R.id.app_size)
    val uninstallButton by bind<TextView>(R.id.uninstall_button)
  }


}