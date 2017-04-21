package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created by jdandrade on 31/01/2017.
 */

@EqualsAndHashCode public abstract class SocialCard implements TimelineCard {

  @Getter private final List<UserTimeline> likes;
  @Getter private final List<CardComment> comments;
  @Getter private final My my;

  public SocialCard(List<UserTimeline> likes, List<CardComment> comments, My my) {
    this.likes = likes;
    this.comments = comments;
    this.my = my;
  }

  public static class CardComment {
    @Getter private long id;
    @Getter private String body;
    @Getter private String name;
    @Getter private String avatar;

    @JsonCreator public CardComment(@JsonProperty("id") long id, @JsonProperty("body") String
        body,
        @JsonProperty("name") String name, @JsonProperty("avatar") String avatar) {
      this.id = id;
      this.body = body;
      this.name = name;
      this.avatar = avatar;
    }
  }
}

