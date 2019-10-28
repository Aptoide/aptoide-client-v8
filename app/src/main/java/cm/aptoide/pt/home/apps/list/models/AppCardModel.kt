package cm.aptoide.pt.home.apps.list.models

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cm.aptoide.pt.R
import cm.aptoide.pt.home.apps.*
import cm.aptoide.pt.home.apps.model.DownloadApp
import cm.aptoide.pt.home.apps.model.InstalledApp
import cm.aptoide.pt.home.apps.model.UpdateApp
import cm.aptoide.pt.networking.image.ImageLoader
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.fa.epoxysample.bundles.models.base.BaseViewHolder
import rx.subjects.PublishSubject

@EpoxyModelClass(layout = R.layout.apps_app_card_item)
abstract class AppCardModel : EpoxyModelWithHolder<AppCardModel.AppCardHolder>() {

  @EpoxyAttribute
  var application: App? = null

  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var eventSubject: PublishSubject<AppClick>? = null


  override fun bind(holder: AppCardHolder) {
    application?.let { app ->
      when (app.type) {
        App.Type.UPDATE -> bindUpdate(holder, app as UpdateApp)
        App.Type.DOWNLOAD -> bindDownloadApp(holder, app as DownloadApp)
        App.Type.INSTALLED -> bindInstalledApp(holder, app as InstalledApp)
        App.Type.APPC_MIGRATION -> bindAppcMigration(holder, app as UpdateApp)
        else -> Unit
      }
    }
  }

  private fun bindAppcMigration(holder: AppCardHolder, app: UpdateApp) {
    holder.nameTextView.text = app.name
    ImageLoader.with(holder.itemView.context).load(app.icon, holder.appIcon)
    holder.secondaryIcon.setImageResource(R.drawable.ic_refresh_appc)
    holder.secondaryIcon.visibility = View.VISIBLE
    holder.secondaryText.setText(R.string.promo_update2appc_appcard_short)
    holder.secondaryText.setTextAppearance(holder.itemView.context,
        R.style.Aptoide_TextView_Medium_XS_AppcOrange)
    holder.actionButton.visibility = View.VISIBLE
    holder.actionButton.setImageResource(R.drawable.ic_update_appc_icon)

    holder.actionButton.setOnClickListener {
      eventSubject?.onNext(AppClick(app, AppClick.ClickType.APPC_DOWNLOAD_APPVIEW))
    }
    holder.itemView.setOnClickListener {
      eventSubject?.onNext(AppClick(app, AppClick.ClickType.CARD_CLICK))
    }
    holder.itemView.setOnLongClickListener {
      eventSubject?.onNext(AppClick(app, AppClick.ClickType.UPDATE_CARD_LONG_CLICK))
      true
    }

  }

  private fun bindInstalledApp(holder: AppCardHolder, installedApp: InstalledApp) {
    holder.nameTextView.text = installedApp.appName
    ImageLoader.with(holder.itemView.context).load(installedApp.icon, holder.appIcon)
    holder.secondaryText.text = installedApp.version
    holder.secondaryText.setTextAppearance(holder.itemView.context,
        R.style.Aptoide_TextView_Regular_XS_BlackAlpha)
    holder.secondaryIcon.visibility = View.GONE
    holder.actionButton.visibility = View.GONE
    holder.actionButton.setOnClickListener(null)
    holder.itemView.setOnClickListener(null)
    holder.itemView.setOnLongClickListener(null)

  }

  private fun bindDownloadApp(holder: AppCardHolder, downloadApp: DownloadApp) {

  }

  private fun bindUpdate(holder: AppCardHolder,
                         app: UpdateApp) {
    holder.nameTextView.text = app.name
    ImageLoader.with(holder.itemView.context).load(app.icon, holder.appIcon)
    holder.secondaryText.text = app.version
    holder.secondaryText.setTextAppearance(holder.itemView.context,
        R.style.Aptoide_TextView_Medium_XS_Grey)
    holder.secondaryIcon.visibility = View.VISIBLE
    holder.secondaryIcon.setImageResource(R.drawable.ic_refresh_orange)
    holder.actionButton.visibility = View.VISIBLE
    holder.actionButton.setImageResource(R.drawable.ic_refresh_action_black)

    holder.actionButton.setOnClickListener {
      eventSubject?.onNext(AppClick(app, AppClick.ClickType.UPDATE_APP))
    }
    holder.itemView.setOnClickListener {
      eventSubject?.onNext(AppClick(app, AppClick.ClickType.CARD_CLICK))
    }
    holder.itemView.setOnLongClickListener {
      eventSubject?.onNext(AppClick(app, AppClick.ClickType.UPDATE_CARD_LONG_CLICK))
      true
    }
  }

  class AppCardHolder : BaseViewHolder() {
    val nameTextView by bind<TextView>(R.id.apps_updates_app_name)
    val appIcon by bind<ImageView>(R.id.apps_updates_app_icon)
    val secondaryText by bind<TextView>(R.id.apps_updates_app_version)
    val secondaryIcon by bind<ImageView>(R.id.secondary_icon)
    val actionButton by bind<ImageView>(R.id.apps_updates_update_button)
  }
}