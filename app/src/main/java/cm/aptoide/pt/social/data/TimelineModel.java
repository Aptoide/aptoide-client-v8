package cm.aptoide.pt.social.data;

import java.util.List;

/**
 * Created by jdandrade on 23/11/2017.
 */

public class TimelineModel {
  private final String timelineVersion;
  private final List<Post> posts;

  public TimelineModel(String timelineVersion, List<Post> posts) {
    this.timelineVersion = timelineVersion;
    this.posts = posts;
  }

  public String getTimelineVersion() {
    return timelineVersion;
  }

  public List<Post> getPosts() {
    return posts;
  }

  public void addPosts(List<Post> posts) {
    this.posts.addAll(posts);
  }
}
