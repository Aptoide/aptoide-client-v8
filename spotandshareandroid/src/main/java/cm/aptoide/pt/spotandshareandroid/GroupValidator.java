package cm.aptoide.pt.spotandshareandroid;

/**
 * Created by filipe on 31-03-2017.
 */

public class GroupValidator {

  public GroupValidator() {
  }

  public boolean validateGroup(String ssid) {
    if (ssid.contains("APTXV")) {
      return true;
    }
    return false;
  }
}
