package cm.aptoide.pt.v8engine.timeline.post;

import cm.aptoide.pt.v8engine.social.data.Card;
import cm.aptoide.pt.v8engine.timeline.PostRepository;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Single;

public class PostManager {

  private final PostRepository postRepository;

  public PostManager(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  public Single<Card> post(String url, String content, String packageName) {
    return postRepository.postOnTimeline(url, content, packageName);
  }

  public Single<List<RelatedApp>> getAppSuggestions(String data) {
    return Single.just(getRandomRelatedApps())
        .delay(300, TimeUnit.MILLISECONDS);
  }

  public Single<PostPreview> getPreview(String data) {
    return Single.just(new PostPreview("", "preview title"))
        .delay(300, TimeUnit.MILLISECONDS);
  }

  private List<RelatedApp> getRandomRelatedApps() {
    LinkedList<RelatedApp> relatedApps = new LinkedList<>();
    for (int i = 0; i < 3; ++i) {
      relatedApps.add(new RelatedApp("", "app " + (i + 1), Origin.Remote, i == 0));
    }
    return relatedApps;
  }

  enum Origin {
    Installed, Remote, Searched
  }

  static class PostPreview {
    private final String image;
    private final String title;

    public PostPreview(String image, String title) {
      this.image = image;
      this.title = title;
    }

    public String getImage() {
      return image;
    }

    public String getTitle() {
      return title;
    }
  }

  static class RelatedApp {
    private final String image;
    private final String name;
    private final Origin origin;
    private boolean selected;

    public RelatedApp(String image, String name, Origin origin, boolean selected) {
      this.image = image;
      this.name = name;
      this.origin = origin;
      this.selected = selected;
    }

    public String getImage() {
      return image;
    }

    public String getName() {
      return name;
    }

    public Origin getOrigin() {
      return origin;
    }

    public boolean isSelected() {
      return selected;
    }

    public void setSelected(boolean selected) {
      this.selected = selected;
    }

    @Override public String toString() {
      return "{name='" + name + '\'' + '}';
    }
  }
}
