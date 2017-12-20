package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Created by jdandrade on 31/01/2017.
 */

public abstract class SocialCard implements TimelineCard {

  private final List<UserTimeline> likes;
  private final List<CardComment> comments;
  private final My my;
  private final Urls urls;

  public SocialCard(List<UserTimeline> likes, List<CardComment> comments, My my,
      @JsonProperty("urls") Urls urls) {
    this.likes = likes;
    this.comments = comments;
    this.my = my;
    this.urls = urls;
  }

  @Override public Urls getUrls() {
    return urls;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $likes = this.likes;
    result = result * PRIME + ($likes == null ? 43 : $likes.hashCode());
    final Object $comments = this.comments;
    result = result * PRIME + ($comments == null ? 43 : $comments.hashCode());
    final Object $my = this.my;
    result = result * PRIME + ($my == null ? 43 : $my.hashCode());
    final Object $urls = this.getUrls();
    result = result * PRIME + ($urls == null ? 43 : $urls.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof SocialCard)) return false;
    final SocialCard other = (SocialCard) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$likes = this.likes;
    final Object other$likes = other.likes;
    if (this$likes == null ? other$likes != null : !this$likes.equals(other$likes)) return false;
    final Object this$comments = this.comments;
    final Object other$comments = other.comments;
    if (this$comments == null ? other$comments != null : !this$comments.equals(other$comments)) {
      return false;
    }
    final Object this$my = this.my;
    final Object other$my = other.my;
    if (this$my == null ? other$my != null : !this$my.equals(other$my)) return false;
    final Object this$urls = this.getUrls();
    final Object other$urls = other.getUrls();
    if (this$urls == null ? other$urls != null : !this$urls.equals(other$urls)) return false;
    return true;
  }

  protected boolean canEqual(Object other) {
    return other instanceof SocialCard;
  }

  public List<UserTimeline> getLikes() {
    return this.likes;
  }

  public List<CardComment> getComments() {
    return this.comments;
  }

  public My getMy() {
    return this.my;
  }

  public static class CardComment {
    private long id;
    private String body;
    private String name;
    private String avatar;
    private long userId;

    @JsonCreator public CardComment(@JsonProperty("id") long id, @JsonProperty("body") String body,
        @JsonProperty("name") String name, @JsonProperty("avatar") String avatar,
        @JsonProperty("user_id") long userId) {
      this.id = id;
      this.body = body;
      this.name = name;
      this.avatar = avatar;
      this.userId = userId;
    }

    public long getId() {
      return this.id;
    }

    public String getBody() {
      return this.body;
    }

    public String getName() {
      return this.name;
    }

    public String getAvatar() {
      return this.avatar;
    }

    public long getUserId() {
      return this.userId;
    }
  }
}

