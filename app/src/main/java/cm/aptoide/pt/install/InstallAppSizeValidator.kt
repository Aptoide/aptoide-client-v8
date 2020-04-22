package cm.aptoide.pt.install

import android.os.Build
import android.os.Environment
import android.os.StatFs
import kotlin.math.roundToLong

class InstallAppSizeValidator {

  fun hasEnoughSpaceToInstallApp(downloadSize: Long): Boolean {
    val bufferedAppSize = getBufferedAppSize(downloadSize)
    val availableSpace = getAvailableSpace()

    return bufferedAppSize < availableSpace
  }

  private fun getAvailableSpace(): Long {
    val stat = StatFs(Environment.getDataDirectory().path)
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
      (stat.availableBlocks * stat.blockSize).toLong()
    } else {
      stat.availableBlocksLong * stat.blockSizeLong
    }
  }

  private fun getBufferedAppSize(appSize: Long): Long {
    val sizePercentage = (appSize * 0.20).roundToLong()
    return appSize + sizePercentage
  }
}