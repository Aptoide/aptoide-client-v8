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

  public void flagGhosts(ArrayList<Group> groupsList) {
    ghostsClearHashmap = new HashMap<>();
    for (int i = 0; i < groupsList.size(); i++) {
      String groupDeviceID = groupsList.get(i)
          .getDeviceID();
      String hotspotCounter = groupsList.get(i)
          .getHotspotControlCounter();

      if (!groupDeviceID.equals("") && !groupsList.get(i)
          .isGhost()) {//to avoid rule 1 - default groupDeviceID = ""
        if (!ghostsClearHashmap.containsKey(groupDeviceID)) {
          ghostsClearHashmap.put(groupDeviceID, groupsList.get(i));
          groupsList.get(i)
              .setGhostFlag(false);
        } else if ((int) hotspotCounter.charAt(0) > (int) ghostsClearHashmap.get(groupDeviceID)
            .getHotspotControlCounter()
            .charAt(0)) {

          ghostsClearHashmap.get(groupDeviceID)
              .setGhostFlag(true);
          //update the new actual host for that groupdeviceid
          ghostsClearHashmap.put(groupDeviceID, groupsList.get(i));
        }
      }
    }
  }

  public ArrayList<Group> removeGhosts(ArrayList<Group> clientsList) {
    ArrayList<Group> clearedList = new ArrayList<>();
    for (int i = 0; i < clientsList.size(); i++) {
      if (!clientsList.get(i)
          .isGhost()) {
        clearedList.add(clientsList.get(i));
      }
    }
    return clearedList;
  }
}
