package cm.aptoide.pt.link;

import android.content.Context;
import cm.aptoide.pt.notification.view.NotificationLink;

/**
 * Created by jdandrade on 05/09/16.
 *
 * This interface represents a factory for creating an {@link Link} implementation Object.
 */
public class LinksHandlerFactory {
  public static final int APPLICATION_TYPE = 1;
  public static final int YOUTUBE_TYPE = 2;
  public static final int CUSTOM_TABS_LINK_TYPE = 3;
  public static final int NOTIFICATION_LINK = 4;
  private final Context context;

  public LinksHandlerFactory(Context context) {
    this.context = context;
  }

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
        return new CustomTabsLink(url, context);
      case NOTIFICATION_LINK:
        return new NotificationLink(url, context);
    }
    return null;
  }
}
