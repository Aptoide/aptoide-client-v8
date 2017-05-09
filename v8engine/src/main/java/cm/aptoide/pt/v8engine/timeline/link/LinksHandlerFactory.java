package cm.aptoide.pt.v8engine.timeline.link;

/**
 * Created by jdandrade on 05/09/16.
 *
 * This interface represents a factory for creating an {@link Link} implementation Object.
 */
public class LinksHandlerFactory {
  public static final int APPLICATION_TYPE = 1;
  public static final int YOUTUBE_TYPE = 2;
  public static final int CUSTOM_TABS_LINK_TYPE = 3;

  /**
   * Returns a {@link Link} implementation. Can be an {@link CustomTabsLink}.
   *
   * @param type Type of the object to return
   * @param url Url to be parsed by the object returned
   *
   * @return {@link Link} implementation.
   */
  public Link get(int type, String url) {

    switch (type) {
      case CUSTOM_TABS_LINK_TYPE:
        return new CustomTabsLink(url);
    }
    return null;
  }
}
