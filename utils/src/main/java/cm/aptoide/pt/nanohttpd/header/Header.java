package cm.aptoide.pt.nanohttpd.header;

import lombok.Data;

/**
 * Class representing a key-value header.
 */
@Data public class Header {

  private String key, value;

  public Header(String key, String value) {
    this.key = key;
    this.value = value;
  }
}
