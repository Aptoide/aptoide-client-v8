package cm.aptoide.pt.timeline.post;

import java.util.regex.Pattern;

/**
 * Created by trinkes on 19/07/2017.
 */

public class UrlValidator {
  private final Pattern urlPattern;

  public UrlValidator(Pattern urlPattern) {
    this.urlPattern = urlPattern;
  }

  public boolean containsUrl(String text) {
    return !getUrl(text).equals("");
  }

  public String getUrl(String text) {
    for (String textPart : text.split("[ \n]")) {
      if (urlPattern.matcher(textPart)
          .matches()) {
        return textPart;
      }
    }
    return "";
  }
}
