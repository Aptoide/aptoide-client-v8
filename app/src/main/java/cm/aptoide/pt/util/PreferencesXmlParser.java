package cm.aptoide.pt.util;

import android.content.res.XmlResourceParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

public class PreferencesXmlParser {

  private final String TAG = PreferencesXmlParser.class.getSimpleName();
  private final String NAMESPACE = "http://schemas.android.com/apk/res/android";
  private final String DEFAULT_VALUE = "defaultValue";
  private final String KEY = "key";

  int eventType = -1;

  public List<String[]> parse(XmlResourceParser parser) throws IOException, XmlPullParserException {
    ArrayList<String[]> data = new ArrayList<>();
    while (eventType != parser.END_DOCUMENT) {
      if (eventType == XmlResourceParser.START_TAG) {
        String defaultValue = parser.getAttributeValue(NAMESPACE, DEFAULT_VALUE);
        String key = parser.getAttributeValue(NAMESPACE, KEY);
        if (defaultValue != null) {
          String[] keyValue = new String[2];
          keyValue[0] = key;
          keyValue[1] = defaultValue;
          data.add(keyValue);
        }
      }
      eventType = parser.next();
    }
    return data;
  }
}
