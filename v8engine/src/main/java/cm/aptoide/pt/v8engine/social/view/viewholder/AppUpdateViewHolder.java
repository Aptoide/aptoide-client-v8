package cm.aptoide.pt.v8engine.social.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.dataprovider.image.ImageLoader;
import cm.aptoide.pt.v8engine.Progress;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.social.data.AppUpdate;
import cm.aptoide.pt.v8engine.social.data.AppUpdateCardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.CardTouchEvent;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import java.util.Date;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 22/06/2017.
 */

public class AppUpdateViewHolder extends CardViewHolder<AppUpdate> {
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final DateCalculator dateCalculator;
  private final ImageView headerIcon;
  private final TextView headerTitle;
  private final TextView headerSubTitle;
  private final ImageView appIcon;
  private final TextView appName;
  private final Button appUpdate;
  private final SpannableFactory spannableFactory;
  private final TextView errorText;

  public AppUpdateViewHolder(View view, PublishSubject<CardTouchEvent> cardTouchEventPublishSubject,
      DateCalculator dateCalculator, SpannableFactory spannableFactory) {
    super(view);
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.dateCalculator = dateCalculator;
    this.spannableFactory = spannableFactory;
    this.headerIcon =
        (ImageView) view.findViewById(R.id.displayable_social_timeline_app_update_card_image);
    this.headerTitle =
        (TextView) view.findViewById(R.id.displayable_social_timeline_app_update_card_title);
    this.headerSubTitle = (TextView) view.findViewById(
        R.id.displayable_social_timeline_app_update_card_card_subtitle);
    this.appIcon = (ImageView) view.findViewById(R.id.displayable_social_timeline_app_update_icon);
    this.appName = (TextView) view.findViewById(R.id.displayable_social_timeline_app_update_name);
    this.appUpdate =
        (Button) view.findViewById(R.id.displayable_social_timeline_recommendation_get_app_button);
    this.errorText =
        (TextView) itemView.findViewById(R.id.displayable_social_timeline_app_update_error);
  }

  @Override void setCard(AppUpdate card, int position) {
    ImageLoader.with(itemView.getContext())
        .loadWithShadowCircleTransform(card.getStoreAvatar(), headerIcon);
    this.headerTitle.setText(getStyledTitle(itemView.getContext(), card.getStoreName()));
    this.headerSubTitle.setText(
        getTimeSinceLastUpdate(itemView.getContext(), card.getUpdateAddedDate()));
    ImageLoader.with(itemView.getContext())
        .load(card.getAppUpdateIcon(), appIcon);
    this.appName.setText(getAppTitle(itemView.getContext(), card.getAppUpdateName()));
    setAppUpdateButtonText(card);
    this.errorText.setVisibility(View.GONE);
    this.appUpdate.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new AppUpdateCardTouchEvent(card, CardTouchEvent.Type.BODY, position)));
  }

  private void setAppUpdateButtonText(AppUpdate card) {
    if (card.getProgress() == Progress.INACTIVE) {
      this.appUpdate.setText(getUpdateAppText(itemView.getContext()).toString()
          .toUpperCase());
    } else if (card.getProgress() == Progress.ACTIVE) {
      this.appUpdate.setText("UPDATING...");
    } else if (card.getProgress() == Progress.DONE) {
      this.appUpdate.setText("UPDATED");
    }
  }

  private Spannable getUpdateAppText(Context context) {
    String application = context.getString(R.string.appstimeline_update_app);
    return spannableFactory.createStyleSpan(
        context.getString(R.string.displayable_social_timeline_app_update_button, application),
        Typeface.NORMAL, application);
  }

  private Spannable getAppTitle(Context context, String appUpdateName) {
    return spannableFactory.createColorSpan(appUpdateName,
        ContextCompat.getColor(context, R.color.black), appUpdateName);
  }

  public String getTimeSinceLastUpdate(Context context, Date updatedDate) {
    return dateCalculator.getTimeSinceDate(context, updatedDate);
  }

  private Spannable getStyledTitle(Context context, String storeName) {
    return spannableFactory.createColorSpan(
        context.getString(R.string.store_has_an_update, storeName),
        ContextCompat.getColor(context, R.color.black_87_alpha), storeName);
  }
}
