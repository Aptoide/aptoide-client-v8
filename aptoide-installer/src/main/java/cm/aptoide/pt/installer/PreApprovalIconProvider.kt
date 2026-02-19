package cm.aptoide.pt.installer

import android.graphics.Bitmap

/**
 * Provides app icon bitmaps, to use in installation pre approvals.
 */
fun interface PreApprovalIconProvider {
  suspend fun getIcon(url: String): Bitmap?
}
