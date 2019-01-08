package cm.aptoide.pt.download;

import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.model.v7.Obb;

public class AppValidator {

  private void validateApp(String md5, Obb appObb, String packageName,
      String appName, String filePath, String filePathAlt) throws IllegalArgumentException {
    if (TextUtils.isEmpty(md5)) {
      throw new IllegalArgumentException("Invalid App MD5");
    }
    if (TextUtils.isEmpty(filePath)) {
      throw new IllegalArgumentException("No main download link provided");
    } else if (TextUtils.isEmpty(filePathAlt)) {
      throw new IllegalArgumentException("No alternative download link provided");
    } else if (appObb != null && appObb.getMain() != null && TextUtils.isEmpty(appObb.getMain()
        .getPath())) {
      throw new IllegalArgumentException("No main obb download link provided");
    } else if (appObb != null && appObb.getPatch() != null && TextUtils.isEmpty(appObb.getPatch()
        .getPath())) {
      throw new IllegalArgumentException("No patch obb download link provided");
    } else if (appObb != null && TextUtils.isEmpty(packageName)) {
      throw new IllegalArgumentException(
          "This app has an OBB and doesn't have the package name specified");
    } else if (TextUtils.isEmpty(appName)) {
      throw new IllegalArgumentException(
          "This app has an OBB and doesn't have the App name specified");
    }
  }


}
