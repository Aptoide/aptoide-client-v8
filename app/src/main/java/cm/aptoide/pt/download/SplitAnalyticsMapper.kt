package cm.aptoide.pt.download

import cm.aptoide.pt.aab.DynamicSplit
import cm.aptoide.pt.database.room.RoomFileToDownload

open class SplitAnalyticsMapper {

  fun getSplitTypesAsString(splitsList: List<RoomFileToDownload>): String {
    val hasBase = splitsList.isNotEmpty()
    var hasPFD = false
    var hasPAD = false

    for (roomFileToDownload in splitsList) {
      if (roomFileToDownload.subFileType == RoomFileToDownload.FEATURE) {
        hasPFD = true
      } else if (roomFileToDownload.subFileType == RoomFileToDownload.ASSET) {
        hasPAD = true
      }
    }
    return buildSplitTypesAnalyticsString(hasBase, hasPFD, hasPAD)
  }

  fun getSplitTypesAsString(hasBase: Boolean, dynamicSplitsList: List<DynamicSplit>): String {
    var hasPFD = false
    var hasPAD = false

    for (dynamicSplit in dynamicSplitsList) {
      if (dynamicSplit.type == "FEATURE") {
        hasPFD = true
      }
      if (dynamicSplit.type == "ASSET") {
        hasPAD = true
      }
    }
    return buildSplitTypesAnalyticsString(hasBase, hasPFD, hasPAD)
  }

  private fun buildSplitTypesAnalyticsString(hasBase: Boolean, hasPFD: Boolean,
                                             hasPAD: Boolean): String {
    var splits = "false"

    if (!hasBase) {
      splits = "false"
    } else if (hasBase && !hasPAD && !hasPFD) {
      splits = "base"
    } else if (hasBase && hasPAD && !hasPFD) {
      splits = "PAD"
    } else if (hasBase && !hasPAD && hasPFD) {
      splits = "PFD"
    } else if (hasBase && hasPAD && hasPFD) {
      splits = "PAD+PFD"
    }
    return splits
  }
}