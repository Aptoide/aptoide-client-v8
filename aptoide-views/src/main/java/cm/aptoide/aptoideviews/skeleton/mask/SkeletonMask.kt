package cm.aptoide.aptoideviews.skeleton.mask

import android.graphics.*
import android.view.View
import cm.aptoide.aptoideviews.skeleton.SkeletonViewPreferences
import kotlin.math.min

/**
 * Responsible for masking a View
 */
internal class SkeletonMask(val view: View, val preferences: SkeletonViewPreferences) {

  private var paint = Paint().apply {
    xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
    color = preferences.shape.color
    style = Paint.Style.FILL
    isAntiAlias = preferences.border.size > 0 || preferences.shape is Shape.Circle
  }
  private val borderPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
    color = preferences.border.color
    strokeWidth = preferences.border.size.toFloat()
    style = Paint.Style.STROKE
    isAntiAlias = preferences.border.size > 0 || preferences.shape is Shape.Circle
  }

  init {
    view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
  }

  fun draw(canvas: Canvas) {
    val rect = RectF(preferences.border.size / 2.0f, preferences.border.size / 2.0f,
        view.width.toFloat() - (preferences.border.size / 2.0f),
        view.height.toFloat() - (preferences.border.size / 2.0f))
    drawShape(rect, preferences.shape, paint, canvas)
    if (preferences.border.size > 0)
      drawShape(rect, preferences.shape, borderPaint, canvas)
  }

  private fun drawShape(bounds: RectF,
                        shape: Shape,
                        paint: Paint, canvas: Canvas) {
    when (shape) {
      is Shape.Rect -> {
        if (shape.cornerRadius > 0) {
          canvas.drawRoundRect(bounds, shape.cornerRadius.toFloat(),
              shape.cornerRadius.toFloat(), paint)
        } else {
          canvas.drawRect(bounds, paint)
        }
      }
      is Shape.Circle -> {
        val cx = (bounds.right - bounds.left) / 2.0f
        val cy = (bounds.bottom - bounds.top) / 2.0f
        canvas.drawCircle(cx, cy, min(cx, cy), paint)
      }
    }
  }

}