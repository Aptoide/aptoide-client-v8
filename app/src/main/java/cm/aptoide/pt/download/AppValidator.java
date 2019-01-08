package cm.aptoide.pt.download;

import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.model.v7.Obb;

public class AppValidator {

  public AppValidationResult validateApp(String md5, Obb appObb, String packageName, String appName,
      String filePath, String filePathAlt) {
    AppValidationResult result = AppValidationResult.VALID_APP;
    if (TextUtils.isEmpty(md5)) {
      result = AppValidationResult.INVALID_MD5;
    }
    if (TextUtils.isEmpty(filePath)) {
      result = AppValidationResult.NO_MAIN_DOWNLOAD_LINK;
    } else if (TextUtils.isEmpty(filePathAlt)) {
      result = AppValidationResult.NO_ALTERNATIVE_DOWNLOAD_LINK;
    } else if (appObb != null && appObb.getMain() != null && TextUtils.isEmpty(appObb.getMain()
        .getPath())) {
      result = AppValidationResult.NO_MAIN_OBB_DOWNLOAD_LINK;
    } else if (appObb != null && appObb.getPatch() != null && TextUtils.isEmpty(appObb.getPatch()
        .getPath())) {
      result = AppValidationResult.NO_PATCH_OBB_DOWNLOAD_LINK;
    } else if (appObb != null && TextUtils.isEmpty(packageName)) {
      result = AppValidationResult.NO_PACKAGE_NAME_SPECIFIED;
    } else if (TextUtils.isEmpty(appName)) {
      result = AppValidationResult.NO_APP_NAME_SPECIFIED;
    }
    return result;
  }

  public enum AppValidationResult {
    INVALID_MD5("Invalid App md5"), NO_MAIN_DOWNLOAD_LINK(
        "No main download link provided"), NO_ALTERNATIVE_DOWNLOAD_LINK(
        "No alternative download link provided"), NO_MAIN_OBB_DOWNLOAD_LINK(
        "No main obb download link provided"), NO_PATCH_OBB_DOWNLOAD_LINK(
        "No patch obb download link provided"), NO_PACKAGE_NAME_SPECIFIED(
        "This app has an OBB and doesn't have the package name specified"), NO_APP_NAME_SPECIFIED(
        "This app has an OBB and doesn't have the App name specified"), VALID_APP(
        "This is a valid app");

    private final String message;

    AppValidationResult(String message) {
      this.message = message;
    }

    public String getMessage() {
      return message;
    }
  }
}
