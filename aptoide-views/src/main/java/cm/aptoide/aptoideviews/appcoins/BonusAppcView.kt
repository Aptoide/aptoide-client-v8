package cm.aptoide.aptoideviews.appcoins

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.widget.FrameLayout
import androidx.annotation.Dimension
import androidx.annotation.Px
import cm.aptoide.aptoideviews.R
import kotlinx.android.synthetic.main.bonus_appc_view.view.*


class BonusAppcView : FrameLayout {

  data class Preferences(val enableCardPadding: Boolean, val side: Side, val size: Size)

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr) {
    val preferences = retrievePreferences(attrs, defStyleAttr)
    val style = if (preferences.side == Side.RIGHT) {
      R.style.BonusAppcViewCardStyle_BorderRight
    } else {
      R.style.BonusAppcViewCardStyle_BorderLeft
    }
    inflate(ContextThemeWrapper(context, style), R.layout.bonus_appc_view, this)
    setupViews()
    applyPreferences(preferences)
    isSaveEnabled = true
  }

  private fun applyPreferences(preferences: Preferences) {
    if (preferences.size == Size.REGULAR) {
      if (!preferences.enableCardPadding) {
        val cardViewParams = card_view.layoutParams
        cardViewParams.width = dpToPx(66).toInt()
        cardViewParams.height = dpToPx(72).toInt()
        card_view.layoutParams = cardViewParams
      }
    } else if (preferences.size == Size.COMPACT) {
      val params = card_view.layoutParams
      params.width = dpToPx(62).toInt()
      params.height = dpToPx(56).toInt()
      card_view.layoutParams = params

      val rootLayoutParams = root_layout.layoutParams
      rootLayoutParams.width = dpToPx(62).toInt()
      rootLayoutParams.height = dpToPx(56).toInt()
      root_layout.layoutParams = rootLayoutParams

      up_to_textview.translationY = dpToPx(2)
      bonus_textview.translationY = dpToPx(-2)
    }

    setSide(preferences.side)
  }

  private fun setupViews() {
    up_to_textview.text = context.getText(R.string.incentives_badge_up_to).toString().toLowerCase()
  }

  private fun retrievePreferences(attrs: AttributeSet?, defStyleAttr: Int): Preferences {
    val typedArray =
        context.obtainStyledAttributes(attrs, R.styleable.BonusAppcView, defStyleAttr, 0)
    val prefs =
        Preferences(typedArray.getBoolean(R.styleable.BonusAppcView_enable_card_padding, true),
            Side.values()[typedArray.getInt(R.styleable.BonusAppcView_side, 0)],
            Size.values()[typedArray.getInt(R.styleable.BonusAppcView_size, 0)])
    typedArray.recycle()
    return prefs

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
  enum class Size { REGULAR, COMPACT }
}