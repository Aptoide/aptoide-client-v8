package cm.aptoide.aptoideviews.skeleton.mask

internal class Size(val width: SizeDimension, val height: SizeDimension)

internal sealed class SizeDimension {
  object OriginalValue : SizeDimension()
  data class PercentValue(val fraction: Float) : SizeDimension()
  data class SpecificValue(val value: Float) : SizeDimension()
}