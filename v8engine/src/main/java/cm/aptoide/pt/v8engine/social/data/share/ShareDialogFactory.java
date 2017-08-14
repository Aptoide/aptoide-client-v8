package cm.aptoide.pt.v8engine.social.data.share;

import android.content.Context;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.pt.v8engine.social.data.AggregatedRecommendation;
import cm.aptoide.pt.v8engine.social.data.AppUpdate;
import cm.aptoide.pt.v8engine.social.data.Media;
import cm.aptoide.pt.v8engine.social.data.PopularApp;
import cm.aptoide.pt.v8engine.social.data.Post;
import cm.aptoide.pt.v8engine.social.data.RatedRecommendation;
import cm.aptoide.pt.v8engine.social.data.Recommendation;
import cm.aptoide.pt.v8engine.social.data.StoreLatestApps;

public class ShareDialogFactory {

  private final Context context;
  private final SharePostViewSetup sharePostViewSetup;

  public ShareDialogFactory(Context context, SharePostViewSetup sharePostViewSetup) {
    this.context = context;
    this.sharePostViewSetup = sharePostViewSetup;
  }

  public ShareDialogInterface createDialogFor(Post post, Account account) {
    // TODO use card type, instead of class instance to filter between share dialogs
    if (post instanceof AggregatedRecommendation) {
      return new AggregatedRecommendationPostShareDialog.Builder(context, sharePostViewSetup,
          account).build();
    } else if (post instanceof AppUpdate) {
      return new AppUpdatePostShareDialog.Builder(context, sharePostViewSetup, account).build();
    } else if (post instanceof Media) {
      return new MediaPostShareDialog.Builder(context, sharePostViewSetup, account).build();
    } else if (post instanceof PopularApp) {
      return new PopularAppPostShareDialog.Builder(context, sharePostViewSetup, account).build();
    } else if (post instanceof RatedRecommendation) {
      return new RatedRecommendationPostShareDialog.Builder(context, sharePostViewSetup,
          account).build();
    } else if (post instanceof Recommendation) {
      return new RecommendationPostShareDialog.Builder(context, sharePostViewSetup,
          account).build();
    } else if (post instanceof StoreLatestApps) {
      return new StoreLatestAppsPostShareDialog.Builder(context, sharePostViewSetup,
          account).build();
    }
    throw new IllegalArgumentException(
        String.format("Post type '%s' does not have a share dialog", post.getClass()));
  }
}
