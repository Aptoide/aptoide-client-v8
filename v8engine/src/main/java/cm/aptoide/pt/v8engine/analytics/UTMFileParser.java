package cm.aptoide.pt.v8engine.analytics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by jdandrade on 01/08/16.
 */
public class UTMFileParser {

  public static final String UTM_SOURCE = "utm_source";
  public static final String UTM_MEDIUM = "utm_medium";
  public static final String UTM_CAMPAIGN = "utm_campaign";
  public static final String UTM_CONTENT = "utm_content";
  public static final String ENTRY_POINT = "entry_point";
  public static final String TAG = UTMFileParser.class.getName();
  private BufferedReader bufferedReader;
  private String utm_line;

  public UTMFileParser(InputStream inputStreamToParse) {
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
