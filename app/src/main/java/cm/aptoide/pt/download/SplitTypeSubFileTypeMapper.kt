package cm.aptoide.pt.download

import cm.aptoide.pt.database.room.RoomFileToDownload

class SplitTypeSubFileTypeMapper {

  fun mapSplitToSubFileType(splitType: String): Int {
    return when {
      splitType.equals("FEATURE") -> {
        RoomFileToDownload.FEATURE
      }
      splitType.equals("ASSET") -> {
        RoomFileToDownload.ASSET
      }
      else -> {
        RoomFileToDownload.SUBTYPE_APK
      }
    }
  }
}