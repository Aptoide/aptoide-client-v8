package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialCard;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jdandrade on 07/08/2017.
 *
 * This is a Dummy implementation for specific items with a specific behavior that are not Posts but
 * we want them in the Post recycler view in Timeline. Features like load More circular Progress
 * (ProgressCard), TimelineLogin/Stats, etc.
 */

public abstract class DummyPost implements Post {
  @Override public String getAbUrl() {
    //supposed to be null
    return null;
  }

  @Override public boolean isLiked() {
    return false;
  }

  @Override public void setLiked(boolean liked) {
    //do nothing
  }

  @Override public boolean isLikeFromClick() {
    return false;
  }

  @Override public List<SocialCard.CardComment> getComments() {
    return new ArrayList<>();
  }

  @Override public long getCommentsNumber() {
    return 0;
  }

  @Override public void addComment(SocialCard.CardComment postComment) {
    //do nothing
  }
}
