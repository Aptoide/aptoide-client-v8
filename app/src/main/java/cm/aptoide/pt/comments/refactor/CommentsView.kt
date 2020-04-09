package cm.aptoide.pt.comments.refactor

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import cm.aptoide.aptoideviews.R
import cm.aptoide.pt.comments.refactor.data.CommentsResponseModel
import cm.aptoide.pt.utils.AptoideUtils
import kotlinx.android.synthetic.main.comments_section.view.*

class CommentsView : FrameLayout, CommentsViewI {

  private lateinit var commentsController: CommentsControler

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr) {
    inflate(context, R.layout.comments_section, this)
    retrievePreferences(attrs, defStyleAttr)
    commentsController = CommentsControler(AptoideUtils.DateTimeU.getInstance(context))
    commentsRecyclerView.setController(commentsController)
  }

  private fun retrievePreferences(attrs: AttributeSet?, defStyleAttr: Int) {
    val typedArray =
        context.obtainStyledAttributes(attrs, R.styleable.CommentsView, defStyleAttr, 0)
    setTitle(typedArray.getString(R.styleable.CommentsView_title) ?: context.getString(
        R.string.comments_title_comments))
    typedArray.recycle()
  }


  override fun setTitle(titleString: String) {
    title.text = titleString
  }

  override fun populateComments(response: CommentsResponseModel) {
    var c = 0
    for (comment in response.comments) {
      c += comment.repliesNr + 1
    }
    count.text = "$c"
    commentsController.setData(false, response)
  }
}