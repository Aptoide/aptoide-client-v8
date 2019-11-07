package cm.aptoide.pt.home.apps.list.models

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cm.aptoide.aptoideviews.downloadprogressview.DownloadEventListener
import cm.aptoide.aptoideviews.downloadprogressview.DownloadProgressView
import cm.aptoide.pt.R
import cm.aptoide.pt.home.apps.AppClick
import cm.aptoide.pt.home.apps.model.DownloadApp
import cm.aptoide.pt.home.apps.model.StateApp
import cm.aptoide.pt.networking.image.ImageLoader
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.fa.epoxysample.bundles.models.base.BaseViewHolder
import rx.subjects.PublishSubject

@EpoxyModelClass(layout = R.layout.apps_download_app_item)
abstract class DownloadCardModel : EpoxyModelWithHolder<DownloadCardModel.CardHolder>() {

  @EpoxyAttribute
  var application: DownloadApp? = null

  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var eventSubject: PublishSubject<AppClick>? = null

  override fun bind(holder: CardHolder) {
    application?.let { app ->
      holder.name.text = app.name
      ImageLoader.with(holder.itemView.context).load(app.icon, holder.appIcon)
      holder.secondaryText.text = app.version

      setupListeners(holder, app)
      processDownload(holder, app)
    }
  }

  private fun setupListeners(holder: CardHolder, app: DownloadApp) {
    holder.itemView.setOnClickListener {
      eventSubject?.onNext(AppClick(app, AppClick.ClickType.CARD_CLICK))
    }
    holder.downloadProgressView.setEventListener(object : DownloadEventListener {
      override fun onActionClick(action: DownloadEventListener.Action) {
        when (action.type) {
          DownloadEventListener.Action.Type.CANCEL -> {
            eventSubject?.onNext(
                AppClick(app, AppClick.ClickType.CANCEL_CLICK))
            setDownloadViewVisibility(holder, false, false)
          }
          DownloadEventListener.Action.Type.RESUME -> eventSubject?.onNext(
              AppClick(app, AppClick.ClickType.RESUME_CLICK))
          DownloadEventListener.Action.Type.PAUSE -> eventSubject?.onNext(
              AppClick(app, AppClick.ClickType.PAUSE_CLICK))
          else -> Unit
        }
      }
    })
  }

  override fun bind(holder: CardHolder, previouslyBoundModel: EpoxyModel<*>) {
    application?.let { app -> processDownload(holder, app) }
  }

  private fun processDownload(holder: CardHolder, app: DownloadApp) {
    when (app.status) {
      StateApp.Status.ACTIVE -> {
        setDownloadViewVisibility(holder, false, false)
        holder.downloadProgressView.startDownload()
      }
      StateApp.Status.INSTALLING -> {
        setDownloadViewVisibility(holder, false, false)
        holder.downloadProgressView.startInstallation()
      }
      StateApp.Status.PAUSE -> {
        setDownloadViewVisibility(holder, false, false)
        holder.downloadProgressView.pauseDownload()
      }
      StateApp.Status.ERROR -> {
        setDownloadViewVisibility(holder, false, true)
      }
      StateApp.Status.IN_QUEUE -> {
        setDownloadViewVisibility(holder, false, false)
      }
      StateApp.Status.STANDBY -> {
        setDownloadViewVisibility(holder, true, false)
      }
      else -> Unit
    }
    holder.downloadProgressView.setProgress(app.progress)
  }


  private fun setDownloadViewVisibility(holder: CardHolder, installed: Boolean,
                                        error: Boolean) {
    if (error) {
      holder.downloadProgressView.visibility = View.GONE
      holder.secondaryIcon.visibility = View.VISIBLE
      holder.secondaryText.visibility = View.VISIBLE
      holder.secondaryIcon.setImageResource(R.drawable.ic_error_outline_red)
      holder.secondaryText.setText(R.string.apps_short_error_download)
      holder.secondaryText.setTextAppearance(holder.itemView.context,
          R.style.Aptoide_TextView_Medium_XS_Red700)
    } else if (installed) {
      holder.downloadProgressView.visibility = View.GONE
      holder.secondaryIcon.visibility = View.GONE
      holder.secondaryText.visibility = View.VISIBLE
      holder.secondaryText.setText(R.string.apps_short_ready_to_install)
      holder.secondaryText.setTextAppearance(holder.itemView.context,
          R.style.Aptoide_TextView_Regular_XS_BlackAlpha)
    } else {
      holder.downloadProgressView.visibility = View.VISIBLE
      holder.secondaryIcon.visibility = View.GONE
      holder.secondaryText.visibility = View.GONE
    }
  }

  class CardHolder : BaseViewHolder() {
    val name by bind<TextView>(R.id.apps_app_name)
    val appIcon by bind<ImageView>(R.id.apps_app_icon)
    val secondaryText by bind<TextView>(R.id.apps_secondary_text)
    val secondaryIcon by bind<ImageView>(R.id.secondary_icon)
    val downloadProgressView by bind<DownloadProgressView>(R.id.download_progress_view)
  }
}