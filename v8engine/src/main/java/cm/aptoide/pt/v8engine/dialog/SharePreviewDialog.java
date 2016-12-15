package cm.aptoide.pt.v8engine.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewInstallDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ArticleDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.StoreLatestAppsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.VideoDisplayable;

/**
 * Created by jdandrade on 09/12/2016.
 */

public class SharePreviewDialog {
  private Displayable displayable;

  public SharePreviewDialog(Displayable cardDisplayable) {
    this.displayable = cardDisplayable;
  }

  public AlertDialog.Builder showPreviewDialog(Context context) {
    AlertDialog.Builder alertadd = new AlertDialog.Builder(context);
    if (displayable instanceof ArticleDisplayable) {
      LayoutInflater factory = LayoutInflater.from(context);
      final View view = factory.inflate(R.layout.displayable_social_timeline_social_article, null);

      TextView title = (TextView) view.findViewById(R.id.card_title);
      TextView subtitle = (TextView) view.findViewById(R.id.card_subtitle);
      ImageView image = (ImageView) view.findViewById(R.id.card_image);
      ImageView userAvatar = (ImageView) view.findViewById(R.id.card_user_avatar);
      TextView articleTitle =
          (TextView) view.findViewById(R.id.partial_social_timeline_thumbnail_title);
      ImageView thumbnail =
          (ImageView) view.findViewById(R.id.partial_social_timeline_thumbnail_image);
      CardView cardView = (CardView) view.findViewById(R.id.card);
      LinearLayout like = (LinearLayout) view.findViewById(R.id.social_like);
      LinearLayout comments = (LinearLayout) view.findViewById(R.id.social_comment);
      TextView relatedTo =
          (TextView) view.findViewById(R.id.partial_social_timeline_thumbnail_related_to);
      CheckBox checkBox = (CheckBox) view.findViewById(R.id.social_preview_checkbox);
      LinearLayout socialTerms = (LinearLayout) view.findViewById(R.id.social_privacy_terms);
      TextView privacyText = (TextView) view.findViewById(R.id.social_text_privacy);

      title.setText(AptoideAccountManager.getUserData().getUserRepo());
      subtitle.setText(AptoideAccountManager.getUserData().getUserName());
      articleTitle.setText(((ArticleDisplayable) displayable).getArticleTitle());
      cardView.setRadius(0);
      like.setClickable(false);
      like.setOnClickListener(null);
      like.setVisibility(View.VISIBLE);
      comments.setVisibility(View.VISIBLE);
      ImageLoader.loadWithShadowCircleTransform(
          AptoideAccountManager.getUserData().getUserAvatarRepo(), image);
      ImageLoader.loadWithShadowCircleTransform(AptoideAccountManager.getUserData().getUserAvatar(),
          userAvatar);
      relatedTo.setVisibility(View.GONE);
      ImageLoader.load(((ArticleDisplayable) displayable).getThumbnailUrl(), thumbnail);

      alertadd.setView(view).setCancelable(false);

      privacyText.setOnClickListener(click -> checkBox.toggle());

      alertadd.setTitle(R.string.social_timeline_you_will_share);
      checkBox.setClickable(true);
      checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
        if (isChecked) {
          userAvatar.setVisibility(View.GONE);
          subtitle.setVisibility(View.GONE);
        } else {
          userAvatar.setVisibility(View.VISIBLE);
          subtitle.setVisibility(View.VISIBLE);
        }
      });
      socialTerms.setVisibility(View.VISIBLE);
    } else if (displayable instanceof VideoDisplayable) {
      LayoutInflater factory = LayoutInflater.from(context);
      final View view = factory.inflate(R.layout.displayable_social_timeline_social_video, null);

      TextView title = (TextView) view.findViewById(R.id.card_title);
      TextView subtitle = (TextView) view.findViewById(R.id.card_subtitle);
      ImageView image = (ImageView) view.findViewById(R.id.card_image);
      ImageView userAvatar = (ImageView) view.findViewById(R.id.card_user_avatar);
      TextView articleTitle =
          (TextView) view.findViewById(R.id.partial_social_timeline_thumbnail_title);
      ImageView thumbnail =
          (ImageView) view.findViewById(R.id.partial_social_timeline_thumbnail_image);
      CardView cardView = (CardView) view.findViewById(R.id.card);
      LinearLayout like = (LinearLayout) view.findViewById(R.id.social_like);
      LinearLayout comments = (LinearLayout) view.findViewById(R.id.social_comment);
      TextView relatedTo =
          (TextView) view.findViewById(R.id.partial_social_timeline_thumbnail_related_to);
      CheckBox checkBox = (CheckBox) view.findViewById(R.id.social_preview_checkbox);
      LinearLayout socialTerms = (LinearLayout) view.findViewById(R.id.social_privacy_terms);
      TextView privacyText = (TextView) view.findViewById(R.id.social_text_privacy);

      title.setText(AptoideAccountManager.getUserData().getUserRepo());
      subtitle.setText(AptoideAccountManager.getUserData().getUserName());
      articleTitle.setText(((VideoDisplayable) displayable).getVideoTitle());
      cardView.setRadius(0);
      like.setClickable(false);
      like.setOnClickListener(null);
      like.setVisibility(View.VISIBLE);
      comments.setVisibility(View.VISIBLE);
      ImageLoader.loadWithShadowCircleTransform(
          AptoideAccountManager.getUserData().getUserAvatarRepo(), image);
      ImageLoader.loadWithShadowCircleTransform(AptoideAccountManager.getUserData().getUserAvatar(),
          userAvatar);
      relatedTo.setVisibility(View.GONE);
      ImageLoader.load(((VideoDisplayable) displayable).getThumbnailUrl(), thumbnail);
      alertadd.setView(view).setCancelable(false);

      privacyText.setOnClickListener(click -> checkBox.toggle());

      alertadd.setTitle(R.string.social_timeline_you_will_share);
      checkBox.setClickable(true);
      checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
        if (isChecked) {
          userAvatar.setVisibility(View.GONE);
          subtitle.setVisibility(View.GONE);
        } else {
          userAvatar.setVisibility(View.VISIBLE);
          subtitle.setVisibility(View.VISIBLE);
        }
      });
      socialTerms.setVisibility(View.VISIBLE);
    } else if (displayable instanceof StoreLatestAppsDisplayable) {
      LayoutInflater factory = LayoutInflater.from(context);
      final View view =
          factory.inflate(R.layout.displayable_social_timeline_social_store_latest_apps, null);

      TextView title = (TextView) view.findViewById(R.id.card_title);
      TextView subtitle = (TextView) view.findViewById(R.id.card_subtitle);
      ImageView image = (ImageView) view.findViewById(R.id.card_image);
      ImageView userAvatar = (ImageView) view.findViewById(R.id.card_user_avatar);
      TextView articleTitle =
          (TextView) view.findViewById(R.id.partial_social_timeline_thumbnail_title);
      ImageView thumbnail =
          (ImageView) view.findViewById(R.id.partial_social_timeline_thumbnail_image);
      CardView cardView = (CardView) view.findViewById(R.id.card);
      LinearLayout like = (LinearLayout) view.findViewById(R.id.social_like);
      LinearLayout comments = (LinearLayout) view.findViewById(R.id.social_comment);
      TextView relatedTo =
          (TextView) view.findViewById(R.id.partial_social_timeline_thumbnail_related_to);
      CheckBox checkBox = (CheckBox) view.findViewById(R.id.social_preview_checkbox);
      LinearLayout socialTerms = (LinearLayout) view.findViewById(R.id.social_privacy_terms);
      TextView privacyText = (TextView) view.findViewById(R.id.social_text_privacy);

      title.setText(AptoideAccountManager.getUserData().getUserRepo());
      subtitle.setText(AptoideAccountManager.getUserData().getUserName());
      articleTitle.setText(((ArticleDisplayable) displayable).getArticleTitle());
      cardView.setRadius(0);
      like.setClickable(false);
      like.setOnClickListener(null);
      like.setVisibility(View.VISIBLE);
      comments.setVisibility(View.VISIBLE);
      ImageLoader.loadWithShadowCircleTransform(
          AptoideAccountManager.getUserData().getUserAvatarRepo(), image);
      ImageLoader.loadWithShadowCircleTransform(AptoideAccountManager.getUserData().getUserAvatar(),
          userAvatar);
      relatedTo.setVisibility(View.GONE);
      ImageLoader.load(((ArticleDisplayable) displayable).getThumbnailUrl(), thumbnail);
      alertadd.setView(view).setCancelable(false);

      privacyText.setOnClickListener(click -> checkBox.toggle());

      alertadd.setTitle(R.string.social_timeline_you_will_share);
      checkBox.setClickable(true);
      checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
        if (isChecked) {
          userAvatar.setVisibility(View.GONE);
          subtitle.setVisibility(View.GONE);
        } else {
          userAvatar.setVisibility(View.VISIBLE);
          subtitle.setVisibility(View.VISIBLE);
        }
      });
      socialTerms.setVisibility(View.VISIBLE);
    } else if (displayable instanceof AppViewInstallDisplayable) {
      LayoutInflater factory = LayoutInflater.from(context);
      final View view =
          factory.inflate(R.layout.displayable_social_timeline_social_recommendation, null);

      TextView title =
          (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_card_title);
      TextView subtitle = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_card_subtitle);
      ImageView image = (ImageView) view.findViewById(R.id.card_image);
      ImageView userAvatar = (ImageView) view.findViewById(R.id.card_user_avatar);
      ImageView appIcon =
          (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
      TextView appName = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_similar_apps);
      TextView appSubTitle =
          (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_name);
      CardView cardView =
          (CardView) view.findViewById(R.id.displayable_social_timeline_recommendation_card);
      LinearLayout like = (LinearLayout) view.findViewById(R.id.social_like);
      LinearLayout comments = (LinearLayout) view.findViewById(R.id.social_comment);
      CheckBox checkBox = (CheckBox) view.findViewById(R.id.social_preview_checkbox);
      LinearLayout socialTerms = (LinearLayout) view.findViewById(R.id.social_privacy_terms);
      TextView privacyText = (TextView) view.findViewById(R.id.social_text_privacy);

      title.setText(AptoideAccountManager.getUserData().getUserRepo());
      subtitle.setText(AptoideAccountManager.getUserData().getUserName());
      ImageLoader.loadWithShadowCircleTransform(((AppViewInstallDisplayable) displayable).getPojo()
          .getNodes()
          .getMeta()
          .getData()
          .getIcon(), appIcon);
      appName.setText(((AppViewInstallDisplayable) displayable).getPojo()
          .getNodes()
          .getMeta()
          .getData()
          .getName());
      appSubTitle.setText(R.string.social_timeline_share_dialog_installed_and_recommended);
      cardView.setRadius(0);
      like.setClickable(false);
      like.setOnClickListener(null);
      like.setVisibility(View.VISIBLE);
      comments.setVisibility(View.VISIBLE);
      ImageLoader.loadWithShadowCircleTransform(
          AptoideAccountManager.getUserData().getUserAvatarRepo(), image);
      ImageLoader.loadWithShadowCircleTransform(AptoideAccountManager.getUserData().getUserAvatar(),
          userAvatar);
      alertadd.setView(view).setCancelable(false);

      privacyText.setOnClickListener(click -> checkBox.toggle());

      alertadd.setTitle(R.string.social_timeline_you_will_share);
      checkBox.setClickable(true);
      checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
        if (isChecked) {
          userAvatar.setVisibility(View.GONE);
          subtitle.setVisibility(View.GONE);
        } else {
          userAvatar.setVisibility(View.VISIBLE);
          subtitle.setVisibility(View.VISIBLE);
        }
      });
      socialTerms.setVisibility(View.VISIBLE);
    }
    return alertadd;
  }
}
