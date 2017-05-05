package cm.aptoide.pt.nanohttpd.misc;

import lombok.Getter;

/**
 * Class representing Internet Media Types.
 */
public enum MimeType {
  APK("application/vnd.android.package-archive", "apk"), HTML("text/html", "html"),;

  @Getter private final String value;
  @Getter private final String extension;

  MimeType(String value, String extension) {
    this.value = value;
    this.extension = extension;
  }
}
