package cm.aptoide.pt.v8engine.social.data.share;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.social.data.CardType;
import cm.aptoide.pt.v8engine.social.data.Media;
import cm.aptoide.pt.v8engine.view.rx.RxAlertDialog;

class MediaPostShareDialog extends BaseShareDialog<Media> {

  static final @LayoutRes int LAYOUT_ID = R.layout.timeline_media_preview;

  MediaPostShareDialog(RxAlertDialog dialog) {
    super(dialog);
  }

  void setupView(View view, Media post) {
    TextView mediaTitle =
        (TextView) view.findViewById(R.id.partial_social_timeline_thumbnail_title);
    ImageView thumbnail = (ImageView) view.findViewById(R.id.featured_graphic);
    TextView relatedTo = (TextView) view.findViewById(R.id.app_name);
    ImageView playIcon = (ImageView) view.findViewById(R.id.play_button);
    if (post.getType()
        .equals(CardType.ARTICLE) || post.getType()
        .equals(CardType.SOCIAL_ARTICLE) || post.getType()
        .equals(CardType.AGGREGATED_SOCIAL_ARTICLE)) {
      playIcon.setVisibility(View.GONE);
    } else if (post.getType()
        .equals(CardType.VIDEO) || post.getType()
        .equals(CardType.SOCIAL_VIDEO) || post.getType()
        .equals(CardType.AGGREGATED_SOCIAL_VIDEO)) {
      playIcon.setVisibility(View.VISIBLE);
    }
    mediaTitle.setMaxLines(1);
    mediaTitle.setText(post.getMediaTitle());
    relatedTo.setVisibility(View.GONE);

    ImageLoader.with(view.getContext())
        .load(post.getMediaThumbnailUrl(), thumbnail);
  }

  public static class Builder extends BaseShareDialog.Builder {

    public Builder(Context context, SharePostViewSetup sharePostViewSetup, Account account) {
      super(context, sharePostViewSetup, account, LAYOUT_ID);
    }

    public MediaPostShareDialog build() {
      return new MediaPostShareDialog(buildRxAlertDialog());
    }
  }
}
