package cm.aptoide.aptoideviews

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class ErrorView : FrameLayout {

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr) {
    inflate(context, R.layout.download_progress_view, this)
    setupClickListeners()
  }

  private fun setupClickListeners() {

  }

}