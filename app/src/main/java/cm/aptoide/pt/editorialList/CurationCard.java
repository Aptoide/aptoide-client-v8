package cm.aptoide.pt.editorialList;

import cm.aptoide.pt.reactions.data.ReactionType;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class CurationCard {
  private final String id;
  private final String subTitle;
  private final String icon;
  private final String title;
  private final String views;
  private List<ReactionType> reactionTypes;
  private String numberOfReactions;
  private ReactionType userReaction;

  public CurationCard(String id, String subTitle, String icon, String title, String views) {
    this.id = id;
    this.subTitle = subTitle;
    this.icon = icon;
    this.title = title;
    this.views = views;
    userReaction = null;
    reactionTypes = Collections.emptyList();
    numberOfReactions = "";
  }

  public String getId() {
    return id;
  }

  public String getSubTitle() {
    return subTitle;
  }

  public String getIcon() {
    return icon;
  }

  public String getTitle() {
    return title;
  }

  public String getViews() {
    return views;
  }

  public List<ReactionType> getReactionTypes() {
    return reactionTypes;
  }

  public void setReactionTypes(List<ReactionType> reactionTypes) {
    this.reactionTypes = reactionTypes;
  }

  public String getNumberOfReactions() {
    return numberOfReactions;
  }

  public void setNumberOfReactions(String numberOfReactions) {
    this.numberOfReactions = numberOfReactions;
  }

  @Nullable public ReactionType getUserReaction() {
    return userReaction;
  }

  public void setUserReaction(ReactionType userReaction) {
    this.userReaction = userReaction;
  }
}
