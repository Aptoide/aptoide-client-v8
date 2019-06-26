package cm.aptoide.pt.home;

import cm.aptoide.pt.reactions.data.TopReaction;
import java.util.Collections;
import java.util.List;

public class ActionItem {
  private final String cardId;
  private final String type;
  private final String title;
  private final String subTitle;
  private final String icon;
  private final String url;
  private final String numberOfViews;
  private final String date;
  private final String captionColor;
  private List<TopReaction> reactionList;
  private int total;
  private String userReaction;

  public ActionItem(String cardId, String type, String title, String subTitle, String icon,
      String url, String numberOfViews, String date, String captionColor) {
    this.cardId = cardId;
    this.type = type;
    this.title = title;
    this.subTitle = subTitle;
    this.icon = icon;
    this.url = url;
    this.numberOfViews = numberOfViews;
    this.date = date;
    this.captionColor = captionColor;
    this.reactionList = Collections.emptyList();
    this.total = -1;
    this.userReaction = "";
  }

  public String getCardId() {
    return cardId;
  }

  public String getType() {
    return type;
  }

  public String getTitle() {
    return title;
  }

  public String getSubTitle() {
    return subTitle;
  }

  public String getIcon() {
    return icon;
  }

  public String getUrl() {
    return url;
  }

  public String getNumberOfViews() {
    return numberOfViews;
  }

  public String getDate() {
    return date;
  }

  public void setReactions(List<TopReaction> topReactionList) {
    this.reactionList = topReactionList;
  }

  public void setNumberOfReactions(int total) {
    this.total = total;
  }

  public List<TopReaction> getReactionList() {
    return reactionList;
  }

  public int getTotal() {
    return total;
  }

  public String getUserReaction() {
    return userReaction;
  }

  public void setUserReaction(String myReaction) {
    this.userReaction = myReaction;
  }

  public String getCaptionColor() {
    return captionColor;
  }
}
