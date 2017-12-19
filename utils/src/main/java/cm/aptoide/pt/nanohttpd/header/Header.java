package cm.aptoide.pt.nanohttpd.header;

/**
 * Class representing a key-value header.
 */
public class Header {

  private String key, value;

  public Header(String key, String value) {
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
