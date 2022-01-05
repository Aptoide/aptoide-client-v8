package cm.aptoide.pt.download

import cm.aptoide.pt.aab.Split
import cm.aptoide.pt.dataprovider.model.v7.Obb

class AppValidator(private val appValidationAnalytics: AppValidationAnalytics) {

  fun validateApp(md5: String?, appObb: Obb?, packageName: String?, appName: String?,
                  filePath: String?, filePathAlt: String?, splits: MutableList<Split>?,
                  requiredSplits: List<String>?): AppValidationResult {
    var result = AppValidationResult.VALID_APP
    if (isStringEmptyOrNull(md5)) {
      result = AppValidationResult.INVALID_MD5
    }
    if (isStringEmptyOrNull(filePath)) {
      appValidationAnalytics.sendInvalidDownloadMainPath(packageName)
      result = AppValidationResult.NO_MAIN_DOWNLOAD_LINK
    } else if (isStringEmptyOrNull(filePathAlt)) {
      appValidationAnalytics.sendInvalidDownloadAlternativePath(packageName)
      result = AppValidationResult.NO_ALTERNATIVE_DOWNLOAD_LINK
    } else if (appObb != null && appObb.main != null && isStringEmptyOrNull(appObb.main
            .path)) {
      appValidationAnalytics.sendInvalidDownloadObbMainPath(packageName)
      result = AppValidationResult.NO_MAIN_OBB_DOWNLOAD_LINK
    } else if (appObb != null && appObb.patch != null && isStringEmptyOrNull(appObb.patch
            .path)) {
      appValidationAnalytics.sendInvalidDownloadObbPatchPath(packageName)
      result = AppValidationResult.NO_PATCH_OBB_DOWNLOAD_LINK
    } else if (appObb != null && isStringEmptyOrNull(packageName)) {
      result = AppValidationResult.NO_PACKAGE_NAME_SPECIFIED
    } else if (isStringEmptyOrNull(appName)) {
      result = AppValidationResult.NO_APP_NAME_SPECIFIED
    }
    if (requiredSplits != null && requiredSplits.isNotEmpty()) {
      for (required in requiredSplits) {
        if (splits!!.none { split -> split.type == required }) {
          result = AppValidationResult.REQUIRED_SPLITS_NOT_FOUND
          break
        }
      }
    }
    return result
  }

  private fun isStringEmptyOrNull(text: String?): Boolean {
    return text == null || text.isEmpty()
  }

  enum class AppValidationResult(val message: String) {
    INVALID_MD5("Invalid App md5"), NO_MAIN_DOWNLOAD_LINK(
        "No main download link provided"),
    NO_ALTERNATIVE_DOWNLOAD_LINK(
        "No alternative download link provided"),
    NO_MAIN_OBB_DOWNLOAD_LINK(
        "No main obb download link provided"),
    NO_PATCH_OBB_DOWNLOAD_LINK(
        "No patch obb download link provided"),
    NO_PACKAGE_NAME_SPECIFIED(
        "This app has an OBB and doesn't have the package name specified"),
    NO_APP_NAME_SPECIFIED(
        "This app has an OBB and doesn't have the App name specified"),
    REQUIRED_SPLITS_NOT_FOUND("Not all required App bundle Splits are being provided"),
    VALID_APP(
        "This is a valid app")
  }
}
