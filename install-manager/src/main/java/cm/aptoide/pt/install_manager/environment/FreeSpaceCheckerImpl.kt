package cm.aptoide.pt.install_manager.environment

import android.os.Environment
import android.os.StatFs

class FreeSpaceCheckerImpl(private val installScaleFactor: Int = 2) : FreeSpaceChecker {

  override fun missingSpace(
    appSize: Long,
    scheduledSize: Long?,
  ): Long = appSize * installScaleFactor + (scheduledSize ?: 0) - availableSpace

  private val availableSpace: Long
    get() = StatFs(Environment.getDataDirectory().path)
      .run { availableBlocksLong * blockSizeLong }
}
