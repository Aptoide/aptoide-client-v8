package cm.aptoide.pt.editorial.epoxy.comments

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import cm.aptoide.pt.R
import com.airbnb.epoxy.ModelView

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class LoadingView : FrameLayout {
  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr) {
    init()
  }

  fun init() {
    inflate(context, R.layout.progress_item, this)
  }
}