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

  private val bitmap: Bitmap? by lazy(LazyThreadSafetyMode.NONE){
    val width = getDimension(view.width, preferences.size.width)
    val height = getDimension(view.height, preferences.size.height)
    if(width > 0 && height > 0)
      Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    else
      null
  }

  private fun getDimension(viewDimension: Int, preferencesDimension: SizeDimension): Int {
    return when(preferencesDimension){
      is SizeDimension.OriginalValue -> viewDimension
      is SizeDimension.PercentValue -> (viewDimension * preferencesDimension.fraction).toInt()
      is SizeDimension.SpecificValue -> preferencesDimension.value.toInt()
    }
  }


  private val bitmapCanvas: Canvas? by lazy(LazyThreadSafetyMode.NONE){
    bitmap?.let { Canvas(it) }
  }

  fun draw(canvas: Canvas) {
    bitmap?.let { mask ->
      canvas.drawBitmap(mask, view.left + offsetLeft, view.top + offsetTop, null)
    }
  }

  fun mask() {
    bitmap?.let { bm ->
      val rect = RectF(0f, 0f, bm.width.toFloat(), bm.height.toFloat())
      drawShape(rect, preferences.shape, paint)
      if(preferences.border.size > 0)
        drawShape(rect, preferences.shape, borderPaint)
    }

  }

  private fun drawShape(bounds: RectF, shape: Shape, paint: Paint) {
    bitmapCanvas?.let { canvas ->
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

}