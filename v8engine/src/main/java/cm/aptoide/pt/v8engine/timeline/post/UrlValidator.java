package cm.aptoide.pt.v8engine.timeline.post;

import java.util.regex.Pattern;

/**
 * Created by trinkes on 19/07/2017.
 */

public class UrlValidator {
  private final Pattern urlPattern;

  public UrlValidator(Pattern url_pattern) {
    urlPattern = url_pattern;
  }

  public boolean containsUrl(String text) {
    return !getUrl(text).equals("");
  }

  public String getUrl(String text) {
    for (String textPart : text.split(" ")) {
      if (urlPattern.matcher(textPart)
          .matches()) {
        return textPart;
      }
    }
    return "";
  }
}
