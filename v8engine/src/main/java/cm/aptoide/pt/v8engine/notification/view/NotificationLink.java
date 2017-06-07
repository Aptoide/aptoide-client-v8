package cm.aptoide.pt.v8engine.notification.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import cm.aptoide.pt.v8engine.link.Link;

/**
 * Created by pedroribeiro on 17/05/17.
 */

public class NotificationLink implements Link {
  private final String url;
  private final Context context;

  public NotificationLink(String url, Context context) {
    this.url = url;
    this.context = context;
  }

  @Override public String getUrl() {
    return url;
  }

  @Override public void launch() {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(Uri.parse(url));
    context.startActivity(intent);
  }
}
