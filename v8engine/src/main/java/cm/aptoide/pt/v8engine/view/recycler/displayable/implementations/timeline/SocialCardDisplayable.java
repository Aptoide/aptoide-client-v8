package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import lombok.Getter;

public abstract class SocialCardDisplayable extends CardDisplayable {

  @Getter private final long numberOfLikes;
  @Getter private final long numberOfComments;
  @Getter private Comment.User user;
  @Getter private Comment.User userSharer;
  @Getter private SpannableFactory spannableFactory;

  SocialCardDisplayable() {
    numberOfLikes = 0;
    numberOfComments = 0;
  }

  SocialCardDisplayable(TimelineCard timelineCard, long numberOfLikes, long numberOfComments,
      Comment.User user, Comment.User userSharer, SpannableFactory spannableFactory) {
    super(timelineCard);
    this.numberOfLikes = numberOfLikes;
    this.numberOfComments = numberOfComments;
    this.userSharer = userSharer;
    this.user = user;
    this.spannableFactory = spannableFactory;
  }

  public Spannable getSharedBy(Context context, String userSharer) {
    return spannableFactory.createColorSpan(
        context.getString(R.string.social_timeline_shared_by, userSharer),
        ContextCompat.getColor(context, R.color.black), userSharer);
  }

  public abstract void like(Context context, String cardType, int rating);
}
