package cm.aptoide.pt.feature_oos.repository

import android.os.Environment
import android.os.StatFs
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AvailableSpaceRepository @Inject constructor() {

  private fun getAvailableSpace(): Long {
    val stat = StatFs(Environment.getDataDirectory().path)
    return stat.availableBlocksLong * stat.blockSizeLong
  }

  fun getRequiredSpace(appSize: Long): Long {
    val availableSpace = getAvailableSpace()
    return 2 * appSize - availableSpace
  }
}
