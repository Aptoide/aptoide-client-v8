package cm.aptoide.pt.search.view.item

import android.view.View
import cm.aptoide.pt.R
import cm.aptoide.pt.dataprovider.model.v7.Malware
import cm.aptoide.pt.home.AppSecondaryInfoViewHolder
import cm.aptoide.pt.networking.image.ImageLoader
import cm.aptoide.pt.search.model.SearchAppResult
import cm.aptoide.pt.search.model.SearchAppResultWrapper
import cm.aptoide.pt.utils.AptoideUtils
import com.jakewharton.rxbinding.view.RxView
import com.jakewharton.rxrelay.PublishRelay
import kotlinx.android.synthetic.main.other_version_row.view.*
import kotlinx.android.synthetic.main.search_app_row.view.*
import kotlinx.android.synthetic.main.search_app_row.view.downloads
import kotlinx.android.synthetic.main.search_app_row.view.store_name
import rx.subscriptions.CompositeSubscription
import java.text.DecimalFormat

class SearchResultViewHolder(itemView: View,
                             private val itemClickSubject: PublishRelay<SearchAppResultWrapper>,
                             val query: String) :
    SearchResultItemView<SearchAppResult?>(itemView) {

  private val appInfoViewHolder: AppSecondaryInfoViewHolder =
      AppSecondaryInfoViewHolder(itemView, DecimalFormat("0.0"))
  private val subscriptions: CompositeSubscription = CompositeSubscription()

  override fun setup(result: SearchAppResult?) {
    result?.let {
      setAppInfo(result)
    }
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
    appInfoViewHolder.setInfo(result.hasAppcBilling(), result.averageRating, false, false)

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

    subscriptions.add(
        RxView.clicks(itemView).doOnNext {
          itemClickSubject.call(SearchAppResultWrapper(query, result, adapterPosition))
        }.subscribe())

  }

  override fun prepareToRecycle() {
    if (subscriptions.hasSubscriptions() && !subscriptions.isUnsubscribed) {
      subscriptions.unsubscribe()
    }
  }

  companion object {
    const val LAYOUT = R.layout.search_app_row
  }
}