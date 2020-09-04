package cm.aptoide.pt.download.view

import cm.aptoide.pt.aab.Split
import cm.aptoide.pt.dataprovider.model.v7.Malware
import cm.aptoide.pt.dataprovider.model.v7.Obb

data class Download(val appId: Long, val appName: String, val packageName: String, val md5: String,
                    val versionName: String, val versionCode: Int, val icon: String,
                    val path: String, val pathAlt: String, val size: Long, val obb: Obb?,
                    val storeName: String, val hasAdvertising: Boolean, val hasBilling: Boolean,
                    val malware: Malware, val splits: List<Split>,
                    val requiredSplits: List<String>, val oemId: String,
                    val downloadModel: DownloadStatusModel?)