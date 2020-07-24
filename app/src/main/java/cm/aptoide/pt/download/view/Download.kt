package cm.aptoide.pt.download.view

import cm.aptoide.pt.aab.Split
import cm.aptoide.pt.dataprovider.model.v7.Malware
import cm.aptoide.pt.dataprovider.model.v7.Obb

interface Download {
  fun getAppId(): Long
  fun getAppName(): String
  fun getPackageName(): String
  fun getMd5(): String
  fun getIcon(): String
  fun getVersionName(): String
  fun getVersionCode(): Int
  fun getPath(): String
  fun getPathAlt(): String
  fun getObb(): Obb
  fun hasAdvertising(): Boolean
  fun hasBilling(): Boolean
  fun getMalware(): Malware
  fun getSize(): Long
  fun getSplits(): List<Split>
  fun getRequiredSplits(): List<String>
  fun getTrustedBadge(): String
  fun getStoreName(): String
  fun getOemId(): String

  fun getDownloadModel(): DownloadStatusModel
}