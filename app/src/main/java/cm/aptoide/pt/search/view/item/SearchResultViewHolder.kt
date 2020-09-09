package cm.aptoide.pt.search.view.item

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cm.aptoide.pt.R
import cm.aptoide.pt.app.view.screenshots.ScreenShotClickEvent
import cm.aptoide.pt.app.view.screenshots.ScreenshotsAdapter
import cm.aptoide.pt.dataprovider.model.v7.Malware
import cm.aptoide.pt.download.view.DownloadClick
import cm.aptoide.pt.download.view.DownloadViewStatusHelper
import cm.aptoide.pt.home.AppSecondaryInfoViewHolder
import cm.aptoide.pt.networking.image.ImageLoader
import cm.aptoide.pt.search.model.SearchAppResult
import cm.aptoide.pt.search.model.SearchAppResultWrapper
import cm.aptoide.pt.utils.AptoideUtils
import cm.aptoide.pt.view.app.AppScreenshot
import kotlinx.android.synthetic.main.search_app_row.view.*
import rx.subjects.PublishSubject
import java.text.DecimalFormat
import java.util.*

class SearchResultViewHolder(itemView: View,
                             private val itemClickSubject: PublishSubject<SearchAppResultWrapper>,
                             private val downloadClickSubject: PublishSubject<DownloadClick>,
                             private val screenShotClick: PublishSubject<ScreenShotClickEvent>,
                             private val query: String?) :
    SearchResultItemView<SearchAppResult?>(itemView) {

  private val downloadViewStatusHelper = DownloadViewStatusHelper(itemView.context)

  private val appInfoViewHolder: AppSecondaryInfoViewHolder =
      AppSecondaryInfoViewHolder(itemView, DecimalFormat("0.0"))

  private val adapter =
      ScreenshotsAdapter(Collections.emptyList(), Collections.emptyList(), screenShotClick, 128)

  init {
    itemView.media_rv.layoutManager =
        LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
    itemView.media_rv.isNestedScrollingEnabled = false
    itemView.media_rv.adapter = adapter
  }

  override fun setup(result: SearchAppResult?) {
    result?.let {
      setAppInfo(result)
      setDownloadStatus(result)
    }
  }

  fun setDownloadStatus(app: SearchAppResult) {
    val downloadModel = app.downloadModel
    if (app.isHighlightedResult && downloadModel != null) {
      downloadViewStatusHelper.setDownloadStatus(app.download, itemView.install_button,
          itemView.download_progress_view)
      setupMediaAdapter(app.screenshots)
      itemView.media_rv.visibility = View.VISIBLE
    } else {
      itemView.install_button.visibility = View.GONE
      itemView.download_progress_view.visibility = View.GONE
      itemView.media_rv.visibility = View.GONE
    }
  }

  private fun setupMediaAdapter(screenshots: List<AppScreenshot>) {
    adapter.updateScreenshots(screenshots)
    adapter.updateVideos(Collections.emptyList())
  }

  private fun setAppInfo(result: SearchAppResult) {
    itemView.app_name.text = result.appName
    itemView.downloads.text = result.totalDownloads.let { AptoideUtils.StringU.withSuffix(it) }
    ImageLoader.with(itemView.app_icon.context).load(result.icon, itemView.app_icon)

    val avgRating = result.averageRating
    if (avgRating <= 0) {
      itemView.rating.setText(R.string.appcardview_title_no_stars)
    } else {
      itemView.rating.visibility = View.VISIBLE
      itemView.rating.text = DecimalFormat("0.0").format(avgRating.toDouble())
    }

    itemView.store_name.text = result.storeName
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
    downloadViewStatusHelper.setupListeners(result.download, downloadClickSubject,
        itemView.install_button,
        itemView.download_progress_view)
  }

  companion object {
    const val LAYOUT = R.layout.search_app_row
  }
}