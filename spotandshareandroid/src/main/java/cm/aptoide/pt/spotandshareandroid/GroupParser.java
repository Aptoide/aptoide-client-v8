package cm.aptoide.pt.spotandshareandroid;

import java.text.ParseException;

/**
 * Created by filipe on 30-03-2017.
 */

public class GroupParser {

  private final static int RULE_1 = 1;
  private final static int RULE_2 = 2;
  private final static int RULE_3 = 3;

  public GroupParser() {
  }

  public Group parse(String groupString) throws ParseException {
    //// TODO: 30-03-2017 filipe check validity of hotspot, else tHROW NEW PARSE EXPCETION

    if (groupString.contains("_")) {
      if (groupString.charAt(5) == ('_')) {//R1
        String deviceName = getDeviceName(RULE_1, groupString);
        return new Group(groupString, deviceName, "", "");//empty string as default values
      } else {//R2
        String deviceName = getDeviceName(RULE_2, groupString);
        String deviceID = getDeviceID(deviceName);
        String hotspotCounter = getHotspotCounter(groupString);

        return new Group(groupString, deviceName, hotspotCounter, deviceID);
      }
    } else {//r3
      String deviceName = getDeviceName(RULE_3, groupString);
      String deviceID = getDeviceID(deviceName);
      String hotspotCounter = getHotspotCounter(groupString);

      return new Group(groupString, deviceName, hotspotCounter, deviceID);
    }
  }

  private String getDeviceName(int rule, String groupString) throws ParseException {
    if (rule == RULE_3) {
      StringBuilder stringBuilder = new StringBuilder();
      for (int i = 12; i < groupString.length(); i++) {
        if (groupString.charAt(i) != '.') {
          stringBuilder.append(groupString.charAt(i));
        }
      }
      return stringBuilder.toString();
    } else {
      String[] tmp = groupString.split("_");
      if (tmp.length > 2) {
        return groupString.split("_")[2];
      } else {
        throw new ParseException("trying to access to an invalid position", 2);
      }
    }
  }

  private String getDeviceID(String deviceName) {
    return String.valueOf(deviceName.charAt(deviceName.length() - 1));
  }

  private String getHotspotCounter(String groupString) {
    return String.valueOf(groupString.charAt(5));
  }
}
