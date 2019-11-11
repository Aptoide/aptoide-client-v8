package cm.aptoide.aptoideviews.skeleton

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.annotation.NonNull
import cm.aptoide.aptoideviews.R
import cm.aptoide.aptoideviews.skeleton.mask.Border
import cm.aptoide.aptoideviews.skeleton.mask.Shape
import cm.aptoide.aptoideviews.skeleton.mask.SkeletonMask


class SkeletonView : View {

  private var viewPreferences: SkeletonViewPreferences = SkeletonViewPreferences()
  private var mask: SkeletonMask? = null

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr) {
    viewPreferences = getSkeletonViewPreferences(context, attrs, viewPreferences)
    mask = SkeletonMask(this, viewPreferences)
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    mask?.draw(canvas)
  }

  companion object {
    private fun getSkeletonViewPreferences(c: Context, attrs: AttributeSet?,
                                           defaultPrefs: SkeletonViewPreferences): SkeletonViewPreferences {
      var skeletonViewPreferences = defaultPrefs
      attrs?.let {
        val t = c.obtainStyledAttributes(it, R.styleable.SkeletonView, 0, 0)
        val shape = getSkeletonShape(t, skeletonViewPreferences.shape)
        val border = getSkeletonBorder(t, skeletonViewPreferences.border)
        skeletonViewPreferences = SkeletonViewPreferences(shape, border)
        t.recycle()
      }
      return skeletonViewPreferences
    }

    private fun getSkeletonBorder(@NonNull t: TypedArray,
                                  defaultBorder: Border): Border {
      val borderColor =
          t.getColorStateList(R.styleable.SkeletonView_skeleton_border_color)?.defaultColor
              ?: defaultBorder.color
      val borderThickness =
          t.getDimension(R.styleable.SkeletonView_skeleton_border_thickness, 0f)
      return Border(borderThickness.toInt(), borderColor)
    }

    private fun getSkeletonShape(@NonNull t: TypedArray,
                                 defaultShape: Shape): Shape {
      var shape: Shape = defaultShape
      val skeletonColor =
          t.getColorStateList(R.styleable.SkeletonView_skeleton_color)?.defaultColor
              ?: defaultShape.color
      val skeletonShape = t.getInt(R.styleable.SkeletonView_skeleton_shape, 0)
      val skeletonCornerRadius =
          t.getDimension(R.styleable.SkeletonView_skeleton_corner_radius, 0f)
      when (skeletonShape) {
        0 -> {
          shape = Shape.Rect(skeletonColor, skeletonCornerRadius.toInt())
        }
        1 -> {
          shape = Shape.Circle(skeletonColor)
        }
      }
      return shape
    }
  }
}