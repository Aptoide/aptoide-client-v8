package cm.aptoide.pt.home.bundles.appcoins

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cm.aptoide.aptoideviews.skeleton.Skeleton
import cm.aptoide.aptoideviews.skeleton.applySkeleton
import cm.aptoide.pt.R
import cm.aptoide.pt.dataprovider.model.v7.Type
import cm.aptoide.pt.home.bundles.base.AppBundleViewHolder
import cm.aptoide.pt.home.bundles.base.FeaturedAppcBundle
import cm.aptoide.pt.home.bundles.base.HomeBundle
import cm.aptoide.pt.home.bundles.base.HomeEvent
import cm.aptoide.pt.utils.AptoideUtils
import kotlinx.android.synthetic.main.bundle_featured_appcoins.view.*
import rx.subjects.PublishSubject
import java.text.DecimalFormat
import java.util.*

class FeaturedAppcViewHolder(val view: View,
                             val oneDecimalFormatter: DecimalFormat,
                             val uiEventsListener: PublishSubject<HomeEvent>) :
    AppBundleViewHolder(view) {

  private val appsInBundleAdapter: FeaturedAppcBundleAdapter =
      FeaturedAppcBundleAdapter(ArrayList(),
          oneDecimalFormatter, uiEventsListener)

  private var skeleton: Skeleton? = null

  init {
    val layoutManager =
        LinearLayoutManager(view.context,
            RecyclerView.HORIZONTAL, false)
    itemView.apps_list.addItemDecoration(object : RecyclerView.ItemDecoration() {
      override fun getItemOffsets(outRect: Rect, view: View,
                                  parent: RecyclerView,
                                  state: RecyclerView.State) {
        val margin = AptoideUtils.ScreenU.getPixelsForDip(5, view.resources)
        outRect[margin, margin, 0] = margin
      }
    })
    itemView.apps_list.layoutManager = layoutManager
    itemView.apps_list.adapter = appsInBundleAdapter
    itemView.apps_list.isNestedScrollingEnabled = false

    val resources = view.context
        .resources
    val windowManager = view.context
        .getSystemService(Context.WINDOW_SERVICE) as WindowManager
    skeleton = itemView.apps_list.applySkeleton(R.layout.app_home_item_skeleton,
        Type.APPS_GROUP.getPerLineCount(resources,
            windowManager) * 3)
  }


  override fun setBundle(homeBundle: HomeBundle?, position: Int) {
    if (homeBundle !is FeaturedAppcBundle) {
      throw IllegalStateException(
          this.javaClass.name + " is getting non FeaturedAppcBundle instance!")
    }
    (homeBundle as? FeaturedAppcBundle)?.let { bundle ->
      if (homeBundle.content == null || homeBundle.bonusPercentage == -1) {
        toggleSkeleton(true)
      } else {
        toggleSkeleton(false)
        itemView.bonus_appc_view.setPercentage(bundle.bonusPercentage)
        appsInBundleAdapter.updateBundle(homeBundle, position)
        appsInBundleAdapter.update(homeBundle.apps)
        itemView.apps_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
          override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dx > 0) {
              uiEventsListener.onNext(
                  HomeEvent(homeBundle, adapterPosition, HomeEvent.Type.SCROLL_RIGHT))
            }
          }
        })

        itemView.see_more_btn.setOnClickListener {
          uiEventsListener.onNext(HomeEvent(homeBundle, adapterPosition, HomeEvent.Type.MORE))
        }
      }
    }
  }

  private fun toggleSkeleton(showSkeleton: Boolean) {
    if (showSkeleton) {
      skeleton?.showSkeleton()
      itemView.title_skeletonview.visibility = View.VISIBLE
      itemView.title.visibility = View.INVISIBLE
      itemView.description_skeletonview.visibility = View.VISIBLE
      itemView.description.visibility = View.INVISIBLE
      itemView.bonus_appc_view.visibility = View.INVISIBLE
      itemView.bonus_appc_skeleton_view.visibility = View.VISIBLE
    } else {
      skeleton?.showOriginal()
      itemView.title_skeletonview.visibility = View.INVISIBLE
      itemView.title.visibility = View.VISIBLE
      itemView.description_skeletonview.visibility = View.INVISIBLE
      itemView.description.visibility = View.VISIBLE
      itemView.bonus_appc_view.visibility = View.VISIBLE
      itemView.bonus_appc_skeleton_view.visibility = View.INVISIBLE
    }
  }
}