package cm.aptoide.aptoideviews.filters

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView

class FiltersRecyclerView @JvmOverloads constructor(context: Context,
                                                    attrs: AttributeSet? = null,
                                                    defStyleAttr: Int = 0) :
    EpoxyRecyclerView(context, attrs, defStyleAttr) {

  override fun createLayoutManager(): LayoutManager {
    return LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
  }
}