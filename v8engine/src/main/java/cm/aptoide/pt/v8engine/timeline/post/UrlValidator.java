package cm.aptoide.pt.v8engine.timeline.post;

import android.util.Patterns;
import java.util.regex.Pattern;

/**
 * Created by trinkes on 19/07/2017.
 */

public class UrlValidator {
  private static final Pattern URL_PATTERN = Patterns.WEB_URL;

  public boolean containsUrl(String text) {
    try {
      getUrl(text);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  public String getUrl(String text) throws IllegalArgumentException {
    for (String textPart : text.split(" ")) {
      if (URL_PATTERN.matcher(textPart)
          .matches()) {
        return textPart;
      }
    }
    throw new IllegalArgumentException(text + " doesn't have any url");
  }
}
