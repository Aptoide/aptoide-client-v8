package cm.aptoide.aptoideviews.recyclerview

import android.graphics.Rect
import android.support.annotation.Px
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

class GridItemSpacingDecorator(@Px var spacingPx: Int = 0) : RecyclerView.ItemDecoration() {

  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                              state: RecyclerView.State) {
    outRect.setEmpty()

    val layout = parent.layoutManager as GridLayoutManager

    val position = parent.getChildAdapterPosition(view)
    val row: Int = position / layout.spanCount

    val marginLeft = if (position % layout.spanCount != 0) spacingPx else 0
    val marginTop = if (row != 0) spacingPx else 0

    outRect.set(marginLeft, marginTop, 0, 0)
  }
}