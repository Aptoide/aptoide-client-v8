package cm.aptoide.pt.download;

import cm.aptoide.pt.dataprovider.model.v7.Obb;

public class AppValidator {

  private AppValidationAnalytics appValidationAnalytics;

  public AppValidator(AppValidationAnalytics appValidationAnalytics) {
    this.appValidationAnalytics = appValidationAnalytics;
  }

  public AppValidationResult validateApp(String md5, Obb appObb, String packageName, String appName,
      String filePath, String filePathAlt) {
    AppValidationResult result = AppValidationResult.VALID_APP;
    if (isStringEmptyOrNull(md5)) {
      result = AppValidationResult.INVALID_MD5;
    }
    if (isStringEmptyOrNull(filePath)) {
      result = AppValidationResult.NO_MAIN_DOWNLOAD_LINK;
    } else if (isStringEmptyOrNull(filePathAlt)) {
      result = AppValidationResult.NO_ALTERNATIVE_DOWNLOAD_LINK;
    } else if (appObb != null && appObb.getMain() != null && isStringEmptyOrNull(appObb.getMain()
        .getPath())) {
      result = AppValidationResult.NO_MAIN_OBB_DOWNLOAD_LINK;
    } else if (appObb != null && appObb.getPatch() != null && isStringEmptyOrNull(appObb.getPatch()
        .getPath())) {
      result = AppValidationResult.NO_PATCH_OBB_DOWNLOAD_LINK;
    } else if (appObb != null && isStringEmptyOrNull(packageName)) {
      result = AppValidationResult.NO_PACKAGE_NAME_SPECIFIED;
    } else if (isStringEmptyOrNull(appName)) {
      result = AppValidationResult.NO_APP_NAME_SPECIFIED;
    }
    return result;
  }

  private boolean isStringEmptyOrNull(String text) {
    return text == null || text.isEmpty();
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
