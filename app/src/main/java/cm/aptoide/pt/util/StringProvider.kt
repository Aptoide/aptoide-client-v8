package cm.aptoide.pt.util

import android.content.Context
import androidx.annotation.StringRes

class StringProvider(val context: Context) {

  fun getString(@StringRes resId: Int): String {
    return context.getString(resId)
  }
}