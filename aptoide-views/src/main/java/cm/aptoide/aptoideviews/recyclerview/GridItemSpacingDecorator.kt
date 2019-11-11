package cm.aptoide.aptoideviews.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

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