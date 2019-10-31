package cm.aptoide.aptoideviews.common

import android.content.Context
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import cm.aptoide.aptoideviews.R

class AnimatedImageView : ImageView {

  var animation: Animatable? = null
  var reverse: Animatable? = null
  var isAnimationsEnabled = true

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr) {
    retrievePreferences(attrs, defStyleAttr)
  }

  private fun retrievePreferences(attrs: AttributeSet?, defStyleAttr: Int) {
    val typedArray =
        context.obtainStyledAttributes(attrs, R.styleable.AnimatedImageView, defStyleAttr, 0)
    val animationId = typedArray.getResourceId(R.styleable.AnimatedImageView_animation, -1)
    val reverseId = typedArray.getResourceId(R.styleable.AnimatedImageView_reverseAnimation, -1)

    if (animationId != -1) {
      animation = AppCompatResources.getDrawable(context, animationId) as Animatable
    }
    if (reverseId != -1) {
      reverse = AppCompatResources.getDrawable(context, reverseId) as Animatable
    }
    typedArray.recycle()
  }

  fun play() {
    animation?.let { anim ->
      setImageDrawable(anim as Drawable)
      if (isAnimationsEnabled) anim.start()
    }
  }

  fun playReverse(){
    reverse?.let { anim ->
      setImageDrawable(anim as Drawable)
      if (isAnimationsEnabled) anim.start()
    }
  }

  fun setReverseAsDefault() {
    setImageDrawable(reverse as Drawable)
  }

}