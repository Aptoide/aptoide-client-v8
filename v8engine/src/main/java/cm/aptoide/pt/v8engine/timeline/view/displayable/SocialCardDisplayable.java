package cm.aptoide.pt.v8engine.timeline.view.displayable;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.view.WindowManager;
import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialCard;
import cm.aptoide.pt.dataprovider.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.dataprovider.model.v7.timeline.UserTimeline;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.timeline.TimelineAnalytics;
import cm.aptoide.pt.v8engine.timeline.view.navigation.AppsTimelineNavigator;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import java.util.Date;
import java.util.List;
import lombok.Getter;

public abstract class SocialCardDisplayable extends CardDisplayable {

  @Getter private final long numberOfLikes;
  @Getter private final long numberOfComments;
  @Getter protected Store store;
  @Getter private Comment.User user;
  @Getter private Comment.User userSharer;
  @Getter private SpannableFactory spannableFactory;
  @Getter private DateCalculator dateCalculator;
  @Getter private Date date;
  @Getter private List<UserTimeline> userLikes;
  @Getter private SocialCard.CardComment latestComment;
  @Getter private boolean liked;
  @Getter private String abUrl;
  private AppsTimelineNavigator timelineNavigator;

  SocialCardDisplayable() {
    numberOfLikes = 0;
    numberOfComments = 0;
  }

  SocialCardDisplayable(TimelineCard timelineCard, long numberOfLikes, long numberOfComments,
      Store store, Comment.User user, Comment.User userSharer, boolean liked,
      List<UserTimeline> userLikes, List<SocialCard.CardComment> comments, Date date,
      SpannableFactory spannableFactory, DateCalculator dateCalculator, String abUrl,
      TimelineAnalytics timelineAnalytics, AppsTimelineNavigator timelineNavigator,
      WindowManager windowManager) {
    super(timelineCard, timelineAnalytics, windowManager);
    this.date = date;
    this.liked = liked;
    this.dateCalculator = dateCalculator;
    this.numberOfLikes = numberOfLikes;
    this.numberOfComments = numberOfComments;
    this.userSharer = userSharer;
    this.user = user;
    this.userLikes = userLikes;
    this.spannableFactory = spannableFactory;
    this.store = store;
    this.abUrl = abUrl;
    this.timelineNavigator = timelineNavigator;
    if (comments.size() > 0) {
      this.latestComment = comments.get(0);
    }
  }

  public String getTimeSinceLastUpdate(Context context) {
    return dateCalculator.getTimeSinceDate(context, date);
  }

  public String getTimeSinceLastUpdate(Context context, Date date) {
    return dateCalculator.getTimeSinceDate(context, date);
  }

  public Spannable getSharedBy(Context context, String userSharer) {
    return spannableFactory.createColorSpan(
        context.getString(R.string.social_timeline_shared_by, userSharer),
        ContextCompat.getColor(context, R.color.black), userSharer);
  }

  public Spannable getBlackHighlightedLike(Context context, String string) {
    return spannableFactory.createColorSpan(
        context.getString(R.string.timeline_short_like_present_singular, string),
        ContextCompat.getColor(context, R.color.black_87_alpha), string);
  }

  public void likesPreviewClick() {
    timelineNavigator.navigateToLikesView(getTimelineCard().getCardId(), numberOfLikes);
  }
}
