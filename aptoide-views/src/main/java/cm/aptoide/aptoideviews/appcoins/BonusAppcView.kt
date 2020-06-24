package cm.aptoide.aptoideviews.appcoins

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.FrameLayout
import androidx.annotation.Dimension
import androidx.annotation.Px
import cm.aptoide.aptoideviews.R
import kotlinx.android.synthetic.main.bonus_appc_view.view.*


class BonusAppcView : FrameLayout {

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr) {
    inflate(context, R.layout.bonus_appc_view, this)
    retrievePreferences(attrs, defStyleAttr)
    isSaveEnabled = true
  }

  private fun retrievePreferences(attrs: AttributeSet?, defStyleAttr: Int) {
    val typedArray =
        context.obtainStyledAttributes(attrs, R.styleable.BonusAppcView, defStyleAttr, 0)

    if (!typedArray.getBoolean(R.styleable.BonusAppcView_enable_card_padding, true)) {
      val params = card_view.layoutParams
      params.width = dpToPx(66).toInt()
      params.height = dpToPx(72).toInt()
      card_view.layoutParams = params
    }

    setSide(Side.values()[typedArray.getInt(R.styleable.BonusAppcView_side, 0)])
    typedArray.recycle()
  }

  fun setSide(side: Side) {
    when (side) {
      Side.LEFT -> root_layout.setBackgroundResource(R.drawable.appc_gradient_right_rounded)
      Side.RIGHT -> root_layout.setBackgroundResource(R.drawable.appc_gradient_left_rounded)
    }
  }

  fun setPercentage(percentage: Int) {
    percentage_number_textview.text = percentage.toString()
  }

  @Px
  private fun dpToPx(@Dimension(unit = Dimension.DP) dp: Int): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),
        resources.displayMetrics)
  }

  enum class Side { LEFT, RIGHT }
}