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
      } else {
        //old version only with app id
        try {
          long id = Long.parseLong(split[0]);
          return new AptoideInstall(id, packageName, false);
        } catch (NumberFormatException e) {
          CrashReport.getInstance()
              .log(e);
        }
      }
    }
    return new AptoideInstall(repo, packageName, showPopup);
  }
}
