package cm.aptoide.pt.dataprovider.model.v7.timeline;

import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;

/**
 * Created by jdandrade on 27/04/2017.
 */

public class PopularApp implements TimelineCard {
  private final String cardId;
  private final Ab ab;
  private final List<Comment.User> users;
  private final Date date;
  private final Urls urls;
  private App popularApplication;

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

  public String getCardId() {
    return this.cardId;
  }

  @Override public Urls getUrls() {
    return urls;
  }

  public Ab getAb() {
    return this.ab;
  }

  public List<Comment.User> getUsers() {
    return this.users;
  }

  public Date getDate() {
    return this.date;
  }

  public App getPopularApplication() {
    return this.popularApplication;
  }
}
