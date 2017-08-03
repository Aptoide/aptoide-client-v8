package cm.aptoide.pt.v8engine.social.data.share;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.timeline.view.LikeButtonView;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import java.util.Date;

public class SharePostViewSetup {

  private DateCalculator dateCalculator;

  public SharePostViewSetup(DateCalculator dateCalculator) {
    this.dateCalculator = dateCalculator;
  }

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
    likeButtonView.setOnClickListener(null);
    likeButtonView.setOnTouchListener(null);
    like.setVisibility(View.VISIBLE);
    likeButtonView.setVisibility(View.VISIBLE);

    comments.setVisibility(View.VISIBLE);
    socialInfoBar.setVisibility(View.GONE);
    socialCommentBar.setVisibility(View.GONE);
  }

  private void setupBottom(View view, Account account) {
    View socialPrivacyTerms = view.findViewById(R.id.social_privacy_terms);
    final boolean accessConfirmed = account.isAccessConfirmed();
    socialPrivacyTerms.setVisibility(accessConfirmed ? View.GONE : View.VISIBLE);
  }

  private void setupHeader(View view, Context context, Account account) {
    if (TextUtils.isEmpty(account.getStore()
        .getName())) {
      setupHeaderWithoutStoreName(view, context, account);
    } else {
      setupHeaderWithStoreName(view, context, account);
    }
  }

  private void setupHeaderWithStoreName(View view, Context context, Account account) {
    TextView storeName = (TextView) view.findViewById(R.id.card_title);
    TextView userName = (TextView) view.findViewById(R.id.card_subtitle);
    TextView date = (TextView) view.findViewById(R.id.card_date);
    ImageView storeAvatar = (ImageView) view.findViewById(R.id.card_image);
    ImageView userAvatar = (ImageView) view.findViewById(R.id.card_user_avatar);

    final String accountStoreName = account.getStore()
        .getName();
    final String accountUserNickname = account.getNickname();
    final String accountUserAvatar = account.getAvatar();
    final String accountStoreAvatar = account.getStore()
        .getAvatar();
    final Account.Access accountUserAccess = account.getAccess();

    storeName.setText(accountStoreName);
    storeName.setTextColor(ContextCompat.getColor(context, R.color.black_87_alpha));

    storeAvatar.setVisibility(View.VISIBLE);
    ImageLoader.with(context)
        .loadWithShadowCircleTransform(accountStoreAvatar, storeAvatar);
    date.setText(dateCalculator.getTimeSinceDate(context, new Date()));
    if (Account.Access.PUBLIC.equals(accountUserAccess)) {
      userAvatar.setVisibility(View.VISIBLE);
      ImageLoader.with(context)
          .loadWithShadowCircleTransform(accountUserAvatar, userAvatar);
      userName.setText(accountUserNickname);
    }
  }

  private void setupHeaderWithoutStoreName(View view, Context context, Account account) {
    TextView storeName = (TextView) view.findViewById(R.id.card_title);
    TextView userName = (TextView) view.findViewById(R.id.card_subtitle);
    ImageView storeAvatar = (ImageView) view.findViewById(R.id.card_image);
    ImageView userAvatar = (ImageView) view.findViewById(R.id.card_user_avatar);

    final String accountStoreName = account.getStore()
        .getName();
    final String accountStoreAvatar = account.getStore()
        .getAvatar();

    storeName.setText(accountStoreName);

    storeAvatar.setVisibility(View.VISIBLE);
    ImageLoader.with(context)
        .loadWithShadowCircleTransform(accountStoreAvatar, storeAvatar);

    userAvatar.setVisibility(View.INVISIBLE);

    userName.setText(account.getNickname());
    userName.setVisibility(View.GONE);
  }

  public void setup(View view, Context context, Account account) {
    setupHeader(view, context, account);
    setupBody(view);
    setupBottom(view, account);
  }
}
