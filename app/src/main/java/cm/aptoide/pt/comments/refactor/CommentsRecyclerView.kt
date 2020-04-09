package cm.aptoide.pt.comments.refactor

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView

class CommentsRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : EpoxyRecyclerView(context, attrs, defStyleAttr) {

  init {
    isNestedScrollingEnabled = false
  }

  override fun createLayoutManager(): LayoutManager {
    // We want setHasFixedSize false because normally this will be inside scrollviews
    return LinearLayoutManager(context)
  }

}