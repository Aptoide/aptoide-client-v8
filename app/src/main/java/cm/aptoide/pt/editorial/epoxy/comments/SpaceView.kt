package cm.aptoide.pt.editorial.epoxy.comments

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.widget.FrameLayout
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView

/**
 * Kind of an hacky way to add space but in some cases it's the only possible way (see usage)
 */
@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class SpaceView : FrameLayout {
  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr) {
  }

  @JvmOverloads
  @ModelProp
  fun setSpaceHeight(height: Int = 0) {
    layoutParams.height = dpToPx(height)
  }

  fun dpToPx(dp: Int): Int {
    val metrics = Resources.getSystem()
        .displayMetrics
    return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
  }
}