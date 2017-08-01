package cm.aptoide.pt.spotandshare.group;

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
    if (groupString.contains("_")) {
      if (groupString.charAt(5) == '_' && groupString.charAt(11) == '_') {
        String deviceName = getDeviceName(RULE_1, groupString);
        return new Group(groupString, deviceName, "", "");//empty string as default values
      } else if (groupString.charAt(0) == '2') {//R2
        String deviceName = getDeviceName(RULE_2, groupString);
        String deviceID = getDeviceID(deviceName);
        String hotspotCounter = getHotspotCounter(groupString);

        return new Group(groupString, deviceName, hotspotCounter, deviceID);
      }
    } else if (groupString.length() == 32 && groupString.charAt(0) == '3') {//R3
      String deviceName = getDeviceName(RULE_3, groupString);
      String deviceID = getDeviceID(deviceName);
      String hotspotCounter = getHotspotCounter(groupString);

      return new Group(groupString, deviceName, hotspotCounter, deviceID);
    }
    throw new ParseException("Could not parse group, doesn't fit any of the defined rules", 0);
  }

  private String getDeviceName(int rule, String groupString) throws ParseException {
    if (rule == RULE_3) {
      StringBuilder stringBuilder = new StringBuilder();
      for (int i = 12; i < groupString.length(); i++) {
        if (groupString.charAt(i) != '.') {
          stringBuilder.append(groupString.charAt(i));
        }
      }
      return stringBuilder.substring(0, stringBuilder.length() - 1);
    } else {
      String[] tmp = groupString.split("_");
      if (tmp.length > 2) {
        String deviceNameWithID = groupString.split("_")[2];
        return deviceNameWithID.substring(0, deviceNameWithID.length() - 1);
      } else {
        throw new ParseException("Trying to access to an invalid position for the device name", 2);
      }
    }
  }

  private String getDeviceID(String deviceName) {
    return String.valueOf(deviceName.charAt(deviceName.length() - 1));
  }

  private String getHotspotCounter(String groupString) throws ParseException {
    if (groupString.length() >= 6) {
      return String.valueOf(groupString.charAt(6));
    } else {
      throw new ParseException("Trying to access to an invalid position for the hotspot counter",
          6);
    }
  }
}
