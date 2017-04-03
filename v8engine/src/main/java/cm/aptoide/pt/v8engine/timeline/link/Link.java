package cm.aptoide.pt.v8engine.timeline.link;

import android.content.Context;

/**
 * Created by jdandrade on 05/09/16.
 */
public interface Link {

  String getUrl();

  void launch(Context context);
}
