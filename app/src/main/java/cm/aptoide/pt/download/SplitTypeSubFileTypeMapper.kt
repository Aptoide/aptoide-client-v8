package cm.aptoide.pt.download

import cm.aptoide.pt.database.room.RoomFileToDownload

class SplitTypeSubFileTypeMapper {

  fun mapSplitToSubFileType(splitType: String): Int {
    return when (splitType) {
      "FEATURE" -> {
        RoomFileToDownload.FEATURE
      }
      "ASSET" -> {
        RoomFileToDownload.ASSET
      }
      else -> {
        RoomFileToDownload.SUBTYPE_APK
      }
    }
  }
}