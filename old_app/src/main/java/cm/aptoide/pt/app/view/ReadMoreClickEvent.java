package cm.aptoide.pt.app.view;

/**
 * Created by franciscocalado on 08/05/18.
 */

public class ReadMoreClickEvent {

  private final boolean hasAppc;
  private final String storeName;
  private final String description;
  private final String storeTheme;

  public ReadMoreClickEvent(String storeName, String description, String storeTheme,
      boolean hasAppc) {
    this.storeName = storeName;
    this.description = description;
    this.storeTheme = storeTheme;
    this.hasAppc = hasAppc;
  }

  public String getStoreName() {
    return storeName;
  }

  public String getDescription() {
    return description;
  }

  public String getStoreTheme() {
    return storeTheme;
  }

  public boolean hasAppc() {
    return hasAppc;
  }
}
