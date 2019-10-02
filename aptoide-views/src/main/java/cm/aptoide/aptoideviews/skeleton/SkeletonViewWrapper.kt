package cm.aptoide.aptoideviews.skeleton

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class SkeletonViewWrapper(val view: View, parent: ViewGroup,
                          skeletonLayoutResId: Int) : Skeleton {

  private val skeletonView: View =
      LayoutInflater.from(parent.context).inflate(skeletonLayoutResId, parent, false)

  init {
    parent.addView(skeletonView)
  }

  override fun showOriginal() {
    skeletonView.visibility = View.GONE
    view.visibility = View.VISIBLE
  }

  override fun showSkeleton() {
    view.visibility = View.GONE
    skeletonView.visibility = View.VISIBLE
  }
}