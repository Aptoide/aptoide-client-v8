package cm.aptoide.pt.updates

import cm.aptoide.pt.database.room.RoomSplit
import cm.aptoide.pt.database.room.RoomUpdate
import cm.aptoide.pt.dataprovider.model.v7.Split
import cm.aptoide.pt.dataprovider.model.v7.listapp.App

class UpdateMapper {

  fun mapAppUpdateList(appList: List<App>): List<RoomUpdate> {
    val updateList: ArrayList<RoomUpdate> = ArrayList()
    for (app in appList) {
      val update: RoomUpdate = mapAppUpdate(app)
      updateList.add(update)
    }
    return updateList
  }

  private fun mapAppUpdate(app: App): RoomUpdate {
    val obb = app.obb
    var mainObbFileName: String? = null
    var mainObbPath: String? = null
    var mainObbMd5: String? = null
    var patchObbFileName: String? = null
    var patchObbPath: String? = null
    var patchObbMd5: String? = null
    if (obb != null) {
      val mainObb = obb.main
      val patchObb = obb.patch
      if (mainObb != null) {
        mainObbFileName = mainObb.filename
        mainObbPath = mainObb.path
        mainObbMd5 = mainObb.md5sum
      }
      if (patchObb != null) {
        patchObbFileName = patchObb.filename
        patchObbPath = patchObb.path
        patchObbMd5 = patchObb.md5sum
      }
    }
    return RoomUpdate(app.id, app.name, app.icon, app.packageName,
        app.file
            .md5sum, app.file
        .path, app.size, app.file
        .vername, app.file
        .pathAlt, app.file
        .vercode, app.file
        .malware
        .rank
        .name, mainObbFileName, mainObbPath, mainObbMd5, patchObbFileName, patchObbPath,
        patchObbMd5, app.hasAdvertising() || app.hasBilling(),
        mapSplits(if (app.hasSplits()) app.aab
            .splits else emptyList()), mapRequiredSplits(
        if (app.hasSplits()) app.aab
            .requiredSplits else emptyList()), app.store
        .name)
  }

  private fun mapSplits(
      splits: List<Split>?): List<RoomSplit>? {
    val splitsResult: MutableList<RoomSplit> = ArrayList()
    if (splits == null) return splitsResult
    for (split in splits) {
      splitsResult.add(
          RoomSplit(split.md5sum, split.path,
              split.type, split.name,
              split.filesize))
    }
    return splitsResult
  }

  private fun mapRequiredSplits(
      requiredSplits: List<String>?): List<String>? {
    val requiredSplitsResult = ArrayList<String>()
    if (requiredSplits == null) return requiredSplitsResult
    for (required in requiredSplits) {
      requiredSplitsResult.add(required)
    }
    return requiredSplitsResult
  }

}