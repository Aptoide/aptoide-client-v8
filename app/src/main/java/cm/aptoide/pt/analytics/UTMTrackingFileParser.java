package cm.aptoide.pt.analytics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by jdandrade on 01/08/16.
 */
public class UTMTrackingFileParser {

  private BufferedReader bufferedReader;
  private String utm_line;

  public UTMTrackingFileParser(InputStream inputStreamToParse) {
    this.bufferedReader = new BufferedReader(new InputStreamReader(inputStreamToParse));
    try {
      utm_line = bufferedReader.readLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns the value associated for the key that is used to perform the search.
   *
   * @param key Key to search in the parsed String in the UTM file.
   *
   * @return String value of the searched key, unknown string if no key found.
   */
  public String valueExtracter(String key) {
    String[] utms = utm_line.split("&");
    for (String utm : utms) {
      if (utm.contains(key)) {
        return utm.substring(key.length() + 1);        // +1 because of =
      }
    }
    return "unknown";
  }
}
