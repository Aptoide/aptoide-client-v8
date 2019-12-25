package cm.aptoide.aptoideviews.swipe

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cm.aptoide.aptoideviews.R

/**
 * This component serves only to add color parameterization through XML, since the original one
 * does not provide it.
 *
 * It's important because otherwise styling it whenever it's used becomes very cumbersome
 * (especially if it's not a direct reference, but an attr instead).
 *
 * The "downside" is that this only supports limited number of colors for the progress color scheme.
 */
class AptoideSwipeRefresh : SwipeRefreshLayout {

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    retrievePreferences(attrs, 0)
  }

  private fun retrievePreferences(attrs: AttributeSet?, defStyleAttr: Int) {
    val typedArray =
        context.obtainStyledAttributes(attrs, R.styleable.AptoideSwipeRefresh, defStyleAttr, 0)

    val progressPrimaryColor =
        typedArray.getColor(R.styleable.AptoideSwipeRefresh_progressPrimaryColor,
            resources.getColor(R.color.default_progress_bar_color))
    val progressSecondaryColor =
        typedArray.getColor(R.styleable.AptoideSwipeRefresh_progressSecondaryColor, -1)
    val progressBackgroundColor =
        typedArray.getColor(R.styleable.AptoideSwipeRefresh_progressBackgroundColor, Color.WHITE)
    setColors(progressPrimaryColor, progressSecondaryColor, progressBackgroundColor)

    typedArray.recycle()
  }

  private fun setColors(progressPrimaryColor: Int, progressSecondaryColor: Int,
                        progressBackgroundColor: Int) {
    if (progressSecondaryColor != -1) {
      setColorSchemeColors(progressPrimaryColor, progressSecondaryColor, progressPrimaryColor,
          progressSecondaryColor)
    } else {
      setColorSchemeColors(progressPrimaryColor)
    }
    setProgressBackgroundColorSchemeColor(progressBackgroundColor)
  }

}