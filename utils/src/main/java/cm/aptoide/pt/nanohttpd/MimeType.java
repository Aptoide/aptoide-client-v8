package cm.aptoide.pt.nanohttpd;

/**
 * Class representing Internet Media Types.
 */
public enum MimeType {
  APK("application/vnd.android.package-archive", "apk"), HTML("text/html", "html"), MIME_PLAINTEXT(
      "text/plain", "txt"), MIME_HTML("text/html", "html"), MIME_JS("application/javascript",
      "js"), MIME_CSS("text/css", "css"), MIME_PNG("image/png", "png"), MIME_SVG("image/svg+xml",
      "svg"), MIME_DEFAULT_BINARY("application/octet-stream", "stream"), MIME_XML("text/xml",
      "xml"),;

  private final String value;
  private final String extension;

  MimeType(String value, String extension) {
    this.value = value;
    this.extension = extension;
  }

  public String getValue() {
    return value;
  }

  public String getExtension() {
    return extension;
  }
}
