package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

/**
 * Created by jdandrade on 23/11/2016.
 */

public class SocialArticleDisplayable extends Displayable {

  @Override public int getViewLayout() {
    return R.layout.displayable_social_timeline_article;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }
}
