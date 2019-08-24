package cm.aptoide.aptoideviews.errors

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.GridLayoutAnimationController

/**
 * Simply an extension of a RecyclerView to make sure that grid animations work correctly
 */
class GridRecyclerView : RecyclerView {

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr)

  override fun setLayoutManager(layout: LayoutManager?) {
    if (layout is GridLayoutManager) {
      super.setLayoutManager(layout)
    } else {
      throw ClassCastException("This GridRecyclerView should only be used with a GridLayoutManager")
    }
  }

  override fun attachLayoutAnimationParameters(child: View?, params: ViewGroup.LayoutParams,
                                               index: Int, count: Int) {
    val layoutManager = layoutManager
    if (adapter != null && layoutManager is GridLayoutManager) {

      var animationParams: GridLayoutAnimationController.AnimationParameters? =
          params.layoutAnimationParameters as? GridLayoutAnimationController.AnimationParameters

      if (animationParams == null) {
        animationParams = GridLayoutAnimationController.AnimationParameters()
        params.layoutAnimationParameters = animationParams
      }

      val columns = layoutManager.spanCount

      animationParams.count = count
      animationParams.index = index
      animationParams.columnsCount = columns
      animationParams.rowsCount = count / columns

      val invertedIndex = count - 1 - index
      animationParams.column = columns - 1 - invertedIndex % columns
      animationParams.row = animationParams.rowsCount - 1 - invertedIndex / columns

    } else {
      super.attachLayoutAnimationParameters(child, params, index, count)
    }
  }
}