package cm.aptoide.aptoideviews.skeletonV2.recyclerview

import androidx.recyclerview.widget.RecyclerView
import cm.aptoide.aptoideviews.skeletonV2.Skeleton

internal class SkeletonRecyclerViewWrapper(private val recyclerView: RecyclerView,
                                           listItemLayoutResId: Int, itemCount: Int) :
    Skeleton {

  private val originalAdapter = recyclerView.adapter
  private val skeletonAdapter = SkeletonAdapter(listItemLayoutResId, itemCount)

  override fun showOriginal() {
    val state = recyclerView.layoutManager?.onSaveInstanceState()
    recyclerView.adapter = originalAdapter
    state?.let { recyclerView.layoutManager?.onRestoreInstanceState(it) }
    recyclerView.startLayoutAnimation()
  }

  override fun showSkeleton() {
    recyclerView.adapter = skeletonAdapter
  }
}