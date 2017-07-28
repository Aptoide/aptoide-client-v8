package cm.aptoide.pt.v8engine.social.data.share;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import cm.aptoide.pt.v8engine.social.data.Post;

interface PartialShareViewPreparer {
  View prepareViewForPostType(Post post, Context context, LayoutInflater factory);
}
