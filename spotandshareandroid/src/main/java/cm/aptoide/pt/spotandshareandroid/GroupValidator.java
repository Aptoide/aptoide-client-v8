package cm.aptoide.pt.spotandshareandroid;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by filipe on 31-03-2017.
 */

public class GroupValidator {

  private HashMap<String, Group> ghostsClearHashmap;

  public GroupValidator() {
  }

  public boolean filterSSID(String ssid) {
    if (ssid.contains("APTXV")) {
      return true;
    }
    return false;
  }

  public ArrayList<Group> removeGhosts(ArrayList<Group> groupsList) {
    ghostsClearHashmap = new HashMap<>();
    for (int i = 0; i < groupsList.size(); i++) {
      String groupDeviceID = groupsList.get(i).getDeviceID();
      String hotspotCounter = groupsList.get(i).getHotspotControlCounter();

      if (!groupDeviceID.equals("")) {//to avoid rule 1
        if (!ghostsClearHashmap.containsKey(groupDeviceID)) {
          ghostsClearHashmap.put(groupDeviceID, groupsList.get(i));
        } else if ((int) hotspotCounter.charAt(0) > (int) ghostsClearHashmap.get(groupDeviceID)
            .getHotspotControlCounter()
            .charAt(0)) {
          ghostsClearHashmap.put(groupDeviceID, groupsList.get(i));
        }
      }
    }

    ArrayList<Group> list = new ArrayList<Group>(ghostsClearHashmap.values());
    return list;
  }
}
