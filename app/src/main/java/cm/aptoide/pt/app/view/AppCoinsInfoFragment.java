package cm.aptoide.pt.app.view;

import android.animation.Animator;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.aptoideviews.socialmedia.SocialMediaView;
import cm.aptoide.aptoideviews.video.YoutubePlayer;
import cm.aptoide.pt.R;
import cm.aptoide.pt.editorial.ScrollEvent;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.AppCoinsInfoPresenter;
import cm.aptoide.pt.view.BackButtonFragment;
import cm.aptoide.pt.view.NotBottomNavigationView;
import com.google.android.material.appbar.AppBarLayout;
import com.jakewharton.rxbinding.support.design.widget.RxAppBarLayout;
import com.jakewharton.rxbinding.support.v4.widget.RxNestedScrollView;
import com.jakewharton.rxbinding.view.RxView;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by D01 on 30/07/2018.
 */

public class AppCoinsInfoFragment extends BackButtonFragment
    implements AppCoinsInfoView, NotBottomNavigationView {

  public static final String NAVIGATE_TO_ESKILLS = "navigateToESkills";
  @Inject AppCoinsInfoPresenter appCoinsInfoPresenter;
  @Inject @Named("screenWidth") float screenWidth;
  @Inject @Named("screenHeight") float screenHeight;
  private Toolbar toolbar;
  private View appCardView;
  private View appCardViewLayout;
  private View bottomAppCardViewLayout;
  private View bottomAppCardView;
  private AppBarLayout appBarLayout;
  private TextView appcMessageAppcoinsSection2a;
  private YoutubePlayer youtubePlayer;
  private Button installButton;
  private Button bottomInstallButton;
  private Button catappultDevButton;
  private NestedScrollView scrollView;
  private TextView appcMessageAppCoinsSection1;
  private TextView appcMessageAppcoinsSection3;
  private TextView appcMessageAppcoinsSection4;
  private View eSkillsViewBackground;
  private SocialMediaView socialMediaView;
  private PublishSubject<Void> eSkillsClick;

  public static AppCoinsInfoFragment newInstance(boolean navigateToESkills) {
    Bundle args = new Bundle();
    args.putBoolean(NAVIGATE_TO_ESKILLS, navigateToESkills);
    AppCoinsInfoFragment fragment = new AppCoinsInfoFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    toolbar = view.findViewById(R.id.toolbar);

    catappultDevButton = view.findViewById(R.id.catappult_dev_button);
    scrollView = view.findViewById(R.id.about_appcoins_scroll);
    appcMessageAppcoinsSection2a = view.findViewById(R.id.appc_message_appcoins_section_2a);
    appcMessageAppcoinsSection3 = view.findViewById(R.id.appc_message_appcoins_section_3);
    appcMessageAppcoinsSection4 = view.findViewById(R.id.appc_message_appcoins_section_4);
    eSkillsViewBackground = view.findViewById(R.id.background_animation);

    youtubePlayer = view.findViewById(R.id.youtube_player);

    appcMessageAppCoinsSection1 = view.findViewById(R.id.appc_message_appcoins_section_1);

    appCardViewLayout = view.findViewById(R.id.app_card_layout);
    appCardView = appCardViewLayout.findViewById(R.id.app_cardview);
    installButton = appCardViewLayout.findViewById(R.id.appview_install_button);

    ((TextView) appCardView.findViewById(R.id.app_title_textview)).setText(
        getString(R.string.appc_title_settings_appcoins_wallet));
    ((ImageView) appCardView.findViewById(R.id.app_icon_imageview)).setImageDrawable(
        ContextCompat.getDrawable(getContext(), R.drawable.appcoins_wallet_icon));

    bottomAppCardViewLayout = view.findViewById(R.id.app_cardview_layout);
    bottomAppCardView = bottomAppCardViewLayout.findViewById(R.id.app_cardview);
    bottomInstallButton = bottomAppCardViewLayout.findViewById(R.id.appview_install_button);

    ((TextView) bottomAppCardView.findViewById(R.id.app_title_textview)).setText(
        getString(R.string.appc_title_settings_appcoins_wallet));
    ((ImageView) bottomAppCardView.findViewById(R.id.app_icon_imageview)).setImageDrawable(
        ContextCompat.getDrawable(getContext(), R.drawable.appcoins_wallet_icon));

    appBarLayout = view.findViewById(R.id.app_bar_layout);
    appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
      float percentage = ((float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange());
      view.findViewById(R.id.appc_header_text)
          .setAlpha(1 - percentage);
      view.findViewById(R.id.app_graphic_guy)
          .setAlpha(1 - percentage);
    });

    socialMediaView = view.findViewById(R.id.social_media_view);

    eSkillsClick = PublishSubject.create();
    setESkillsTextView();

    setHasOptionsMenu(true);
    setupToolbar();
    setupBottomAppBar();
    youtubePlayer.loadVideo("uwoq5sLzZrs", true);
    attachPresenter(appCoinsInfoPresenter);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  private void setESkillsTextView() {
    String baseMessage = getString(R.string.appc_info_view_eskills_body);
    String clickMessage = getString(R.string.appc_info_view_eskills_body_button);
    String eSkillsMessage = String.format(baseMessage, clickMessage);

    SpannableString eSkillsSpan = new SpannableString(eSkillsMessage);
    ClickableSpan eSkillsClickSpan = new ClickableSpan() {
      @Override public void onClick(View view) {
        eSkillsClick.onNext(null);
      }
    };
    eSkillsSpan.setSpan(eSkillsClickSpan, eSkillsMessage.indexOf(clickMessage),
        eSkillsMessage.indexOf(clickMessage) + clickMessage.length(),
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    appcMessageAppcoinsSection4.setText(eSkillsSpan);
    appcMessageAppcoinsSection4.setMovementMethod(LinkMovementMethod.getInstance());
  }

  private void setupBottomAppBar() {
    bottomAppCardView.getViewTreeObserver()
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override public void onGlobalLayout() {
            if (!isAppItemShown()) {
              addBottomCardAnimation();
            }
            bottomAppCardView.getViewTreeObserver()
                .removeOnGlobalLayoutListener(this);
          }
        });
  }

  @Override public void onDestroy() {
    super.onDestroy();
    eSkillsClick = null;
  }

  @Override public void onDestroyView() {
    toolbar = null;
    appCardView = null;
    installButton = null;
    bottomInstallButton = null;
    catappultDevButton = null;
    appcMessageAppCoinsSection1 = null;
    appcMessageAppcoinsSection2a = null;
    appcMessageAppcoinsSection3 = null;
    appcMessageAppcoinsSection4 = null;
    eSkillsViewBackground = null;
    youtubePlayer = null;
    scrollView = null;
    socialMediaView = null;
    super.onDestroyView();
  }

  private void setupTextView(String text, TextView appcMessageAppcoinsSection, Object... args) {
    appcMessageAppcoinsSection.setText(
        Html.fromHtml(String.format(text, args), getImageGetter(), null));
  }

  private void setupToolbar() {
    toolbar.setTitle(R.string.appc_title_about_appcoins);
    toolbar.setTitleTextColor(Color.WHITE);
    toolbar.setSubtitleTextColor(Color.WHITE);
    AppCompatActivity appCompatActivity = ((AppCompatActivity) getActivity());
    appCompatActivity.setSupportActionBar(toolbar);
    ActionBar actionBar = appCompatActivity.getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  @NonNull private Html.ImageGetter getImageGetter() {
    return source -> {
      Drawable drawable = null;
      try {
        drawable = getResources().getDrawable(Integer.parseInt(source));
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
      } catch (Resources.NotFoundException e) {
        Log.e("log_tag", "Image not found. Check the ID.", e);
      } catch (NumberFormatException e) {
        Log.e("log_tag", "Source string not a valid resource ID.", e);
      }
      return drawable;
    };
  }

  @Override public Observable<Void> cardViewClick() {
    return RxView.clicks(appCardView);
  }

  @Override public Observable<Void> catappultButtonClick() {
    return RxView.clicks(catappultDevButton);
  }

  @Override public Observable<Void> installButtonClick() {
    return Observable.merge(RxView.clicks(installButton), RxView.clicks(bottomInstallButton));
  }

  @Override public Observable<Void> appCoinsWalletLinkClick() {
    return RxView.clicks(appcMessageAppcoinsSection2a);
  }

  @Override public void openApp(String packageName) {
    AptoideUtils.SystemU.openApp(packageName, getContext().getPackageManager(), getContext());
  }

  @Override public void setButtonText(boolean isInstalled) {
    String installState = getResources().getString(R.string.appview_button_install);
    if (isInstalled) {
      installButton.setText(getResources().getString(R.string.appview_button_open));
      bottomInstallButton.setText(getResources().getString(R.string.appview_button_open));
    } else {
      installButton.setText(installState);
      bottomInstallButton.setText(installState);
    }
  }

  @Override public Observable<ScrollEvent> appItemVisibilityChanged() {
    return Observable.mergeDelayError(RxNestedScrollView.scrollChangeEvents(scrollView),
        RxAppBarLayout.offsetChanges(appBarLayout))
        .map(scrollDown -> isAppItemShown())
        .map(ScrollEvent::new)
        .distinctUntilChanged(ScrollEvent::getItemShown);
  }

  @Override public void removeBottomCardAnimation() {
    configureAppCardAnimation(bottomAppCardViewLayout, appCardViewLayout, 0f, 300, true);
  }

  @Override public void addBottomCardAnimation() {
    configureAppCardAnimation(appCardViewLayout, bottomAppCardViewLayout, 0.1f, 300, false);
  }

  @Override public Observable<SocialMediaView.SocialMediaType> socialMediaClick() {
    return socialMediaView.onSocialMediaClick();
  }

  @Override public void setBonusAppc(int bonusAppc) {
    appcMessageAppCoinsSection1.setText(
        String.format(getString(R.string.appc_info_view_body_1_variable), bonusAppc));

    setupTextView(getString(R.string.appc_info_view_title_5_variable), appcMessageAppcoinsSection3,
        bonusAppc, getAppCoinsLogoString());
  }

  @Override public void setNoBonusAppcView() {
    appcMessageAppCoinsSection1.setText(getString(R.string.appc_info_view_body_1_variable_no_data));
    setupTextView(getString(R.string.appc_info_view_title_5_variable_no_data),
        appcMessageAppcoinsSection3, getAppCoinsLogoString());
  }

  @Override public Observable<Void> eSkillsClick() {
    return eSkillsClick;
  }

  @Override public void focusOnESkillsSection() {
    Animation blinkAnimation =
        AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.animation_blink);
    eSkillsViewBackground.startAnimation(blinkAnimation);
    scrollView.smoothScrollTo(0, appcMessageAppcoinsSection4.getBottom());
  }

  private String getAppCoinsLogoString() {
    String format = "<img width='24px' height='20px' src=\"%1$s\"/>";
    return String.format(format, R.drawable.ic_spend_appc);
  }

  private void configureAppCardAnimation(View layoutToHide, View layoutToShow, float hideScale,
      int duration, boolean isRemoveBottomCard) {
    layoutToHide.animate()
        .scaleY(hideScale)
        .scaleX(hideScale)
        .alpha(0)
        .setDuration(duration)
        .setListener(new Animator.AnimatorListener() {
          @Override public void onAnimationStart(Animator animator) {
            layoutToShow.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1)
                .setDuration(duration)
                .setListener(new Animator.AnimatorListener() {
                  @Override public void onAnimationStart(Animator animator) {
                    layoutToShow.setVisibility(View.VISIBLE);
                  }

                  @Override public void onAnimationEnd(Animator animator) {

                  }

                  @Override public void onAnimationCancel(Animator animator) {

                  }

                  @Override public void onAnimationRepeat(Animator animator) {

                  }
                })
                .start();
          }

          @Override public void onAnimationEnd(Animator animator) {
            if (isRemoveBottomCard) {
              layoutToHide.setVisibility(View.INVISIBLE);
            }
          }

          @Override public void onAnimationCancel(Animator animator) {

          }

          @Override public void onAnimationRepeat(Animator animator) {

          }
        })
        .start();
  }

  public boolean isAppItemShown() {
    final Rect placeHolderPosition = new Rect();
    appCardView.getLocalVisibleRect(placeHolderPosition);
    final Rect screen =
        new Rect(0, 0, (int) screenWidth, (int) screenHeight - appCardView.getHeight() * 2);
    return placeHolderPosition.intersect(screen);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_appcoins_info, container, false);
  }
}
