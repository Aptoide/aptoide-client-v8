package cm.aptoide.pt.home.bundles.appcoins

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cm.aptoide.pt.home.bundles.base.AppBundle
import cm.aptoide.pt.home.bundles.base.AppBundleViewHolder
import cm.aptoide.pt.home.bundles.base.HomeBundle
import cm.aptoide.pt.home.bundles.base.HomeEvent
import cm.aptoide.pt.utils.AptoideUtils
import kotlinx.android.synthetic.main.bundle_earn_appcoins.view.*
import rx.subjects.PublishSubject
import java.text.DecimalFormat

class EarnAppCoinsViewHolder(val view: View,
                             decimalFormatter: DecimalFormat,
                             val uiEventsListener: PublishSubject<HomeEvent>) :
    AppBundleViewHolder(view) {

  private var adapter: EarnAppCoinsListAdapter =
      EarnAppCoinsListAdapter(decimalFormatter, uiEventsListener)

  init {

    val layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
    itemView.apps_list.addItemDecoration(object : RecyclerView.ItemDecoration() {

      override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                  state: RecyclerView.State) {
        val margin = AptoideUtils.ScreenU.getPixelsForDip(8, view.resources)
        val marginBottom = AptoideUtils.ScreenU.getPixelsForDip(4, view.resources)
        outRect.set(margin, margin, 0, marginBottom)
      }
    })
    itemView.apps_list.layoutManager = layoutManager
    itemView.apps_list.adapter = adapter
    itemView.apps_list.isNestedScrollingEnabled = false
    itemView.apps_list.setHasFixedSize(true)
  }


  override fun setBundle(homeBundle: HomeBundle?, position: Int) {
    if (homeBundle !is AppBundle) {
      throw IllegalStateException(this.javaClass.name + " is getting non AppBundle instance!")
    }
    adapter.updateBundle(homeBundle, position)
    itemView.apps_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dx > 0) {
          uiEventsListener.onNext(
              HomeEvent(homeBundle, adapterPosition, HomeEvent.Type.SCROLL_RIGHT))
        }
      }
    })
    itemView.setOnClickListener {
      uiEventsListener.onNext(HomeEvent(homeBundle, adapterPosition, HomeEvent.Type.APPC_KNOW_MORE))
    }

    itemView.see_more_btn.setOnClickListener {
      uiEventsListener.onNext(HomeEvent(homeBundle, adapterPosition, HomeEvent.Type.MORE))
    }
  }
}