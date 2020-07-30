package cm.aptoide.pt.search.view.item

import android.view.View
import cm.aptoide.pt.R
import cm.aptoide.pt.dataprovider.model.v7.Malware
import cm.aptoide.pt.download.view.DownloadClick
import cm.aptoide.pt.download.view.DownloadViewStatusHelper
import cm.aptoide.pt.home.AppSecondaryInfoViewHolder
import cm.aptoide.pt.networking.image.ImageLoader
import cm.aptoide.pt.search.model.SearchAppResult
import cm.aptoide.pt.search.model.SearchAppResultWrapper
import cm.aptoide.pt.utils.AptoideUtils
import kotlinx.android.synthetic.main.search_app_row.view.*
import rx.subjects.PublishSubject
import java.text.DecimalFormat

class SearchResultViewHolder(itemView: View,
                             private val itemClickSubject: PublishSubject<SearchAppResultWrapper>,
                             private val downloadClickSubject: PublishSubject<DownloadClick>,
                             private val query: String?) :
    SearchResultItemView<SearchAppResult?>(itemView) {

  private val downloadViewStatusHelper = DownloadViewStatusHelper(itemView.context)

  private val appInfoViewHolder: AppSecondaryInfoViewHolder =
      AppSecondaryInfoViewHolder(itemView, DecimalFormat("0.0"))

  override fun setup(result: SearchAppResult?) {
    result?.let {
      setAppInfo(result)
      setDownloadStatus(result)
    }
  }


  fun setDownloadStatus(app: SearchAppResult) {
    val downloadModel = app.getDownloadModel()
    if (app.isHighlightedResult && downloadModel != null) {
      downloadViewStatusHelper.setDownloadStatus(app, itemView.install_button,
          itemView.download_progress_view)
    } else {
      itemView.install_button.visibility = View.GONE
      itemView.download_progress_view.visibility = View.GONE
    }
  }

  private fun setAppInfo(result: SearchAppResult) {
    itemView.app_name.text = result.getAppName()
    itemView.downloads.text = result.totalDownloads.let { AptoideUtils.StringU.withSuffix(it) }
    ImageLoader.with(itemView.app_icon.context).load(result.getIcon(), itemView.app_icon)

    val avgRating = result.averageRating
    if (avgRating <= 0) {
      itemView.rating.setText(R.string.appcardview_title_no_stars)
    } else {
      itemView.rating.visibility = View.VISIBLE
      itemView.rating.text = DecimalFormat("0.0").format(avgRating.toDouble())
    }

    itemView.store_name.text = result.getStoreName()
    appInfoViewHolder.setInfo(result.hasBilling() || result.hasAdvertising(), result.averageRating,
        false, false)

    when (result.rank) {
      Malware.Rank.TRUSTED.ordinal -> {
        ImageLoader.with(itemView.app_badge.context)
            .load(R.drawable.ic_badges_trusted, itemView.app_badge)
      }
      Malware.Rank.CRITICAL.ordinal -> {
        ImageLoader.with(itemView.app_badge.context)
            .load(R.drawable.ic_badges_critical, itemView.app_badge)
      }
      Malware.Rank.WARNING.ordinal -> {
        ImageLoader.with(itemView.app_badge.context)
            .load(R.drawable.ic_badges_warning, itemView.app_badge)
      }
      Malware.Rank.UNKNOWN.ordinal -> {
        ImageLoader.with(itemView.app_badge.context)
            .load(R.drawable.ic_badges_unknown, itemView.app_badge)
      }
    }

    itemView.setOnClickListener {
      itemClickSubject.onNext(SearchAppResultWrapper(query, result, adapterPosition))
    }
    downloadViewStatusHelper.setupListeners(result, downloadClickSubject, itemView.install_button,
        itemView.download_progress_view)
  }

  companion object {
    const val LAYOUT = R.layout.search_app_row
  }
}