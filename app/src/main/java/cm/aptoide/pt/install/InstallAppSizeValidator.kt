package cm.aptoide.pt.install

import android.os.Build
import android.os.Environment
import android.os.StatFs

class InstallAppSizeValidator {

  fun hasEnoughSpaceToInstallApp(downloadSize: Long): Boolean {
    val availableSpace = getAvailableSpace()
    return downloadSize <= availableSpace
  }

  private fun getAvailableSpace(): Long {
    val stat = StatFs(Environment.getDataDirectory().path)
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
      (stat.availableBlocks * stat.blockSize).toLong()
    } else {
      stat.availableBlocksLong * stat.blockSizeLong
    }
  }

}