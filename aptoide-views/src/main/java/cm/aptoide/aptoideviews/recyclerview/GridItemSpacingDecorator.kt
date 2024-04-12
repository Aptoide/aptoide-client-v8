package cm.aptoide.aptoideviews.recyclerview

import android.graphics.Rect
import android.os.Build
import android.text.TextUtils
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class GridItemSpacingDecorator(@Px var spacingPx: Int = 0) : RecyclerView.ItemDecoration() {

  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                              state: RecyclerView.State) {
    outRect.setEmpty()

    val layout = parent.layoutManager as GridLayoutManager

    val position = parent.getChildAdapterPosition(view)
    val row: Int = position / layout.spanCount

    val marginLeft = if (position % layout.spanCount != 0) spacingPx else 0
    val marginTop = if (row != 0) spacingPx else 0
    val isLeftToRight =
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_LTR
      } else {
        true
      }
    if(isLeftToRight) {
      outRect.set(marginLeft, marginTop, 0, 0)
    } else {
      outRect.set(0, marginTop, marginLeft, 0)
    }
  }
}