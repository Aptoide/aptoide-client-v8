package cm.aptoide.aptoideviews.errors

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import cm.aptoide.aptoideviews.R
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.error_view.view.*
import rx.Observable

class ErrorView : FrameLayout {

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr) {
    inflate(context, R.layout.error_view, this)
    setError(Error.GENERIC)
  }

  /**
   * Sets an action for when the retry button is clicked.
   * This exists mainly as a compatibility method for legacy code.
   */
  @Deprecated(message = "This function exists for legacy code. Do not use this.",
      replaceWith = ReplaceWith(expression = "retryClick()",
          imports = ["cm.aptoide.aptoideviews.errors.ErrorView"]))
  fun setRetryAction(action: () -> Unit) {
    retry.setOnClickListener { action() }
  }

  /**
   * Emits everytime the retry button is clicked
   */
  fun retryClick(): Observable<Void> {
    return RxView.clicks(retry)
  }

  /**
   * Sets the error state for the view
   * @see Error
   */
  fun setError(error: Error) {
    when (error) {
      Error.GENERIC -> {
        error_image.setImageResource(R.drawable.generic_error)
        error_text.setText(R.string.error503)
      }
      Error.NO_NETWORK -> {
        error_image.setImageResource(R.drawable.no_connection_error)
        error_text.setText(R.string.could_not_connect_internet)
      }
    }
  }

  enum class Error {
    GENERIC, NO_NETWORK
  }

}