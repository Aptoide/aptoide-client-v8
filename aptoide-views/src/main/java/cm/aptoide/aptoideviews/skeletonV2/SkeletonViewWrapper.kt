package cm.aptoide.aptoideviews.skeletonV2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class SkeletonViewWrapper(val view: View, parent: ViewGroup,
                          skeletonLayoutResId: Int) : Skeleton {

  private val skeletonView: View =
      LayoutInflater.from(parent.context).inflate(skeletonLayoutResId, parent, false)

  override fun showOriginal() {
    skeletonView.visibility = View.INVISIBLE
    view.visibility = View.VISIBLE
  }

  override fun showSkeleton() {
    view.visibility = View.INVISIBLE
    skeletonView.visibility = View.VISIBLE
  }
}