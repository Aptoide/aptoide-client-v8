package cm.aptoide.pt.link;

import android.content.Context;
import lombok.Getter;

/**
 * Created by jdandrade on 06/09/16.
 */
public class CustomTabsLink implements Link {

  private final Context context;
  @Getter private String url;

  public CustomTabsLink(String url, Context context) {
    this.url = url;
    this.context = context;
  }

  @Override public void launch() {
    CustomTabsHelper.getInstance()
        .openInChromeCustomTab(this.url, context);
  }
}
