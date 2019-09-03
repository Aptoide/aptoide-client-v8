package cm.aptoide.aptoideviews.skeleton

import android.graphics.Color
import cm.aptoide.aptoideviews.skeleton.mask.Border
import cm.aptoide.aptoideviews.skeleton.mask.Shape
import cm.aptoide.aptoideviews.skeleton.mask.Size
import cm.aptoide.aptoideviews.skeleton.mask.SizeDimension

internal data class SkeletonViewPreferences(
    var size: Size = Size(SizeDimension.OriginalValue, SizeDimension.OriginalValue),
    var shape: Shape = Shape.Rect(Color.parseColor("#EDEEF2"), 0),
    var border: Border = Border(0, Color.WHITE))