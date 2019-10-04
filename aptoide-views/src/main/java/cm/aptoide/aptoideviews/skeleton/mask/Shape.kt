package cm.aptoide.aptoideviews.skeleton.mask

import androidx.annotation.ColorInt
import androidx.annotation.Px

internal sealed class Shape(@ColorInt val color: Int) {
  class Rect(@ColorInt color: Int, @Px val cornerRadius: Int) : Shape(color)
  class Circle(@ColorInt color: Int) : Shape(color)
}