package cm.aptoide.pt.aptoide_installer.data.download.filedownloader

import android.text.TextUtils
import cm.aptoide.pt.utils.AptoideUtils
import java.io.File

class Md5Comparator(private val generalFolderPath: String) {

  fun compareMd5(md5: String, fileName: String): Boolean {
    return TextUtils.equals(
      AptoideUtils.AlgorithmU.computeMd5(File(generalFolderPath + fileName)), md5
    )
  }
}