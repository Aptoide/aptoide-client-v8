package cm.aptoide.pt.dataprovider.model.v7.timeline;

import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;
import lombok.Getter;

/**
 * Created by jdandrade on 27/04/2017.
 */

public class PopularApp implements TimelineCard {
  @Getter private final String cardId;
  @Getter private final Ab ab;
  @Getter private final List<Comment.User> users;
  @Getter private final Date date;
  private final Urls urls;
  @Getter private App popularApplication;

  @JsonCreator PopularApp(@JsonProperty("uid") String cardId, @JsonProperty("ab") Ab ab,
      @JsonProperty("apps") List<App> popularApps,
      @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC") @JsonProperty("date") Date date,
      @JsonProperty("users") List<Comment.User> users, @JsonProperty("urls") Urls urls) {
    this.cardId = cardId;
    this.ab = ab;
    this.users = users;
    this.date = date;
    this.urls = urls;
    if (popularApps.size() > 0) {
      this.popularApplication = popularApps.get(0);
    }
  }

  @Override public Urls getUrls() {
    return urls;
  }
}
