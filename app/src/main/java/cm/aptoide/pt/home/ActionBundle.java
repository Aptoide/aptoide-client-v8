package cm.aptoide.pt.home;

import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.reactions.data.ReactionType;
import java.util.Collections;
import java.util.List;

public class ActionBundle implements HomeBundle {
  private final String title;
  private final BundleType type;
  private final Event event;
  private final String tag;
  private final ActionItem actionItem;
  private List<ReactionType> reactionTypes;
  private String numberOfReactions;
  private ReactionType userReaction;

  public ActionBundle(String title, BundleType type, Event event, String tag,
      ActionItem actionItem) {
    this.title = title;
    this.type = type;
    this.event = event;
    this.tag = tag;
    this.actionItem = actionItem;
    userReaction = null;
    reactionTypes = Collections.emptyList();
    numberOfReactions = "";
  }

  @Override public String getTitle() {
    return this.title;
  }

  @Override public List<?> getContent() {
    return Collections.emptyList();
  }

  @Override public BundleType getType() {
    return this.type;
  }

  @Override public Event getEvent() {
    return this.event;
  }

  @Override public String getTag() {
    return this.tag;
  }

  public ActionItem getActionItem() {
    return actionItem;
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

  public ReactionType getUserReaction() {
    return userReaction;
  }

  public void setUserReaction(ReactionType userReaction) {
    this.userReaction = userReaction;
  }
}
