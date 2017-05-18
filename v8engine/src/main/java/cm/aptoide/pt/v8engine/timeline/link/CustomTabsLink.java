package cm.aptoide.pt.v8engine.timeline.link;

import android.content.Context;
import lombok.Getter;

/**
 * Created by jdandrade on 06/09/16.
 */
public class CustomTabsLink implements Link {

  @Getter private String url;

  public CustomTabsLink(String url) {
    this.url = url;
  }

  @Override public void launch(Context context) {
    CustomTabsHelper.getInstance()
        .openInChromeCustomTab(this.url, context);
  }
}
