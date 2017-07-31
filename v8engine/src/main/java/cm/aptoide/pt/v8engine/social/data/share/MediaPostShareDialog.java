package cm.aptoide.pt.v8engine.social.data.share;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.social.data.CardType;
import cm.aptoide.pt.v8engine.social.data.Media;
import cm.aptoide.pt.v8engine.view.rx.RxAlertDialog;
import rx.Observable;

class MediaPostShareDialog implements DialogInterface {

  private RxAlertDialog dialog;

  public MediaPostShareDialog(RxAlertDialog dialog) {
    this.dialog = dialog;
  }

  @Override public void cancel() {
    dialog.cancel();
  }

  @Override public void dismiss() {
    dialog.dismiss();
  }

  public void show() {
    dialog.show();
  }

  public Observable<DialogInterface> cancelsSelected() {
    return dialog.cancels();
  }

  public static class Builder {

    private final RxAlertDialog.Builder builder;
    private final LayoutInflater layoutInflater;
    private final Context context;
    private final SharePostViewSetup sharePostViewSetup;
    private final Account account;
    private Media post;

    public Builder(Context context, SharePostViewSetup sharePostViewSetup, Account account) {
      this.builder = new RxAlertDialog.Builder(context);
      layoutInflater = LayoutInflater.from(context);
      this.context = context;
      this.sharePostViewSetup = sharePostViewSetup;
      this.account = account;
    }

    public MediaPostShareDialog.Builder setPost(Media post) {
      this.post = post;
      return this;
    }

    public MediaPostShareDialog build() {
      View view = getView();
      sharePostViewSetup.setup(view, context, account);
      builder.setView(view);
      final RxAlertDialog dialog = builder.build();
      return new MediaPostShareDialog(dialog);
    }

    private View getView() {
      View view = layoutInflater.inflate(R.layout.timeline_media_preview, null);
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
      ImageLoader.with(context)
          .load(post.getMediaThumbnailUrl(), thumbnail);
      return view;
    }
  }
}
