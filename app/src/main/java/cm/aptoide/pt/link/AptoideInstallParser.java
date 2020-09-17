package cm.aptoide.pt.link;

import cm.aptoide.pt.crashreports.CrashReport;

/**
 * Created by trinkes on 04/12/2017.
 */

public class AptoideInstallParser {
  public AptoideInstall parse(String substring) {
    substring = substring.replace("\"", "");
    String[] split = substring.split("&");
    String repo = null;
    String packageName = null;
    String uname = "";
    String openType = null;
    boolean showPopup = false;
    for (String property : split) {
      if (property.toLowerCase()
          .contains("package")) {
        packageName = property.split("=")[1];
      } else if (property.toLowerCase()
          .contains("store")) {
        repo = property.split("=")[1];
      } else if (property.toLowerCase()
          .contains("show_install_popup")) {
        showPopup = property.split("=")[1].equals("true");
      } else if (property.toLowerCase()
          .contains("open_type")) {
        openType = property.split("=")[1];
      } else if (property.toLowerCase()
          .contains("uname")) {
        uname = property.split("=")[1];
        return new AptoideInstall(uname, packageName);
      } else {
        //old version only with app id
        try {
          long id = Long.parseLong(split[0]);
          return new AptoideInstall(id, packageName, null);
        } catch (NumberFormatException e) {
          CrashReport.getInstance()
              .log(e);
        }
      }
    }
    // Show_install_popup is a type of open_type, however it already existed before open_type
    // If someone still uses it, this ensures that if both are set, the older param has priority
    if (showPopup) {
      openType = "open_with_install_popup";
    }
    return new AptoideInstall(repo, packageName, openType);
  }
}
