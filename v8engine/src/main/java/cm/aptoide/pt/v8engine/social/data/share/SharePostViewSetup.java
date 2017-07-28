package cm.aptoide.pt.v8engine.social.data.share;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.timeline.view.LikeButtonView;

class SharePostViewSetup {
  private void setupBody(View view) {
    CardView cardView = (CardView) view.findViewById(R.id.card);
    LinearLayout like = (LinearLayout) view.findViewById(R.id.social_like);
    LikeButtonView likeButtonView = (LikeButtonView) view.findViewById(R.id.social_like_button);
    TextView comments = (TextView) view.findViewById(R.id.social_comment);
    LinearLayout socialInfoBar = (LinearLayout) view.findViewById(R.id.social_info_bar);
    LinearLayout socialCommentBar =
        (LinearLayout) view.findViewById(R.id.social_latest_comment_bar);

    cardView.setRadius(8);
    cardView.setCardElevation(10);
    like.setOnClickListener(null);
    like.setOnTouchListener(null);
    like.setVisibility(View.VISIBLE);
    likeButtonView.setOnClickListener(null);
    likeButtonView.setOnTouchListener(null);
    likeButtonView.setVisibility(View.VISIBLE);

    comments.setVisibility(View.VISIBLE);
    socialInfoBar.setVisibility(View.GONE);
    socialCommentBar.setVisibility(View.GONE);

    // yet to be used...
    // LinearLayout socialTerms = (LinearLayout) view.findViewById(R.id.social_privacy_terms);
    // TextView privacyText = (TextView) view.findViewById(R.id.social_text_privacy);
    // TextView numberOfComments = (TextView) view.findViewById(R.id.social_number_of_comments);
  }

  private void setupBottom(View view, Account account) {
    CheckBox checkBox = (CheckBox) view.findViewById(R.id.social_preview_checkbox);
    checkBox.setVisibility(account.isAccessConfirmed() ? View.GONE : View.VISIBLE);
  }

  private void setupHeader(View view, Context context, Account account) {
    TextView storeName = (TextView) view.findViewById(R.id.card_title);
    TextView userName = (TextView) view.findViewById(R.id.card_subtitle);
    ImageView storeAvatar = (ImageView) view.findViewById(R.id.card_image);
    ImageView userAvatar = (ImageView) view.findViewById(R.id.card_user_avatar);

    if (account.getStore()
        .getName() != null) {
      storeName.setTextColor(ContextCompat.getColor(context, R.color.black_87_alpha));
      if (Account.Access.PUBLIC.equals(account.getAccess())) {
        storeAvatar.setVisibility(View.VISIBLE);
        userAvatar.setVisibility(View.VISIBLE);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(account.getStore()
                .getAvatar(), storeAvatar);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(account.getAvatar(), userAvatar);
        storeName.setText(account.getStore()
            .getName());
        userName.setText(account.getNickname());
      } else {
        storeAvatar.setVisibility(View.VISIBLE);
        userAvatar.setVisibility(View.INVISIBLE);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(account.getStore()
                .getAvatar(), storeAvatar);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(account.getAvatar(), userAvatar);
        storeName.setText(account.getStore()
            .getName());
        userName.setText(account.getNickname());
        userName.setVisibility(View.GONE);
      }
    }
  }

  public void setup(View view, Context context, Account account) {
    setupHeader(view, context, account);
    setupBody(view);
    setupBottom(view, account);
  }
}
