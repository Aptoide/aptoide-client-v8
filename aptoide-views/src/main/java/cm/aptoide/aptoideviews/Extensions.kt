package cm.aptoide.aptoideviews

import android.view.View
import android.view.ViewGroup

internal fun <T1 : Any, T2 : Any, R : Any> safeLet(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
  return if (p1 != null && p2 != null) block(p1, p2) else null
}

internal fun ViewGroup.childViews(): List<View> = (0 until childCount).map { getChildAt(it) }
