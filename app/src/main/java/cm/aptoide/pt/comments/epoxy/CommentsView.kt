package cm.aptoide.pt.comments.epoxy

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import cm.aptoide.aptoideviews.R
import kotlinx.android.synthetic.main.comments_section.view.*

class CommentsView : FrameLayout, CommentsViewInterface {

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr) {
    inflate(context, R.layout.comments_section, this)
  }

  override fun setTitle(titleString: String) {
    title.text = titleString
  }
}