package cm.aptoide.pt.install_manager.environment

import android.os.Environment
import android.os.StatFs

class DeviceStorageImpl : DeviceStorage {

  override val availableFreeSpace: Long
    get() = StatFs(Environment.getDataDirectory().path)
      .run { availableBlocksLong * blockSizeLong }
}
