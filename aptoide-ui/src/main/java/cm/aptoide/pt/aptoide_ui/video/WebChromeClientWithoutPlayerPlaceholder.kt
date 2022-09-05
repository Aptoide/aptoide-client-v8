package cm.aptoide.pt.aptoide_ui.video

import android.graphics.Bitmap
import android.webkit.WebChromeClient

class WebChromeClientWithoutPlayerPlaceholder : WebChromeClient() {
  override fun getDefaultVideoPoster(): Bitmap? {
    return Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
  }
}