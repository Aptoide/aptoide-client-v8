package cm.aptoide.aptoideviews.skeleton.mask

import android.graphics.*
import android.view.View
import cm.aptoide.aptoideviews.skeleton.SkeletonViewPreferences
import kotlin.math.min

/**
 * Responsible for masking a View
 */
internal class SkeletonMask(val view: View, val offsetLeft: Float, val offsetTop: Float, val preferences: SkeletonViewPreferences) {

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

  private val bitmap: Bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.RGB_565)
  private val bitmapCanvas: Canvas = Canvas(bitmap)

  fun draw(canvas: Canvas) {
    canvas.drawBitmap(bitmap, view.left + offsetLeft, view.top + offsetTop, null)
  }

  fun mask() {
    val rect = RectF(0f, 0f, view.width.toFloat(), view.height.toFloat())
    drawShape(rect, preferences.shape, paint)
    if(preferences.border.size > 0)
      drawShape(rect, preferences.shape, borderPaint)
  }

  private fun drawShape(bounds: RectF, shape: Shape, paint: Paint) {
    when (shape) {
      is Shape.Rect -> {
        if (shape.cornerRadius > 0) {
          bitmapCanvas.drawRoundRect(bounds, shape.cornerRadius.toFloat(),
              shape.cornerRadius.toFloat(), paint)
        } else {
          bitmapCanvas.drawRect(bounds, paint)
        }
      }
      is Shape.Circle -> {
        val cx = (bounds.right - bounds.left) / 2.0f
        val cy = (bounds.bottom - bounds.top) / 2.0f
        bitmapCanvas.drawCircle(cx, cy, min(cx, cy), paint)
      }
    }
  }

}