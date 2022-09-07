package cm.aptoide.pt.home;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.bottomNavigation.BottomNavigationActivity;
import cm.aptoide.pt.bottomNavigation.BottomNavigationItem;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.promotions.PromotionsHomeDialog;
import cm.aptoide.pt.themes.ThemeManager;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.jakewharton.rxbinding.view.RxView;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

public class HomeContainerFragment extends NavigationTrackFragment implements HomeContainerView {

  private static final BottomNavigationItem BOTTOM_NAVIGATION_ITEM = BottomNavigationItem.HOME;
  @Inject HomeContainerPresenter presenter;
  @Inject ThemeManager themeManager;
  private BottomNavigationActivity bottomNavigationActivity;
  private CheckBox gamesChip;
  private CheckBox appsChip;
  private AppBarLayout appBarLayout;
  private ImageView userAvatar;
  private ImageView promotionsIcon;
  private TextView promotionsTicker;
  private PromotionsHomeDialog promotionsHomeDialog;
  private EskillsHomeDialog eskillsHomeDialog;
  private LoggedInTermsAndConditionsDialog gdprDialog;

  private PublishSubject<ChipsEvents> chipCheckedEvent;

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity instanceof BottomNavigationActivity) {
      bottomNavigationActivity = ((BottomNavigationActivity) activity);
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    chipCheckedEvent = PublishSubject.create();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    userAvatar = view.findViewById(R.id.user_actionbar_icon);
    promotionsIcon = view.findViewById(R.id.promotions_icon);
    promotionsTicker = view.findViewById(R.id.promotions_ticker);
    promotionsHomeDialog = new PromotionsHomeDialog(getContext());
    eskillsHomeDialog = new EskillsHomeDialog(getContext());
    gdprDialog = new LoggedInTermsAndConditionsDialog(getContext());
    if (bottomNavigationActivity != null) {
      bottomNavigationActivity.requestFocus(BOTTOM_NAVIGATION_ITEM);
    }
    gamesChip = view.findViewById(R.id.games_chip);
    appsChip = view.findViewById(R.id.apps_chip);
    appBarLayout = view.findViewById(R.id.app_bar_layout);

    setupChipsListeners();
    attachPresenter(presenter);
  }

  @Override public void onResume() {
    super.onResume();
    ChipsEvents checked = ChipsEvents.HOME;
    if (gamesChip.isChecked()) {
      checked = ChipsEvents.GAMES;
    } else if (appsChip.isChecked()) checked = ChipsEvents.APPS;
    chipCheckedEvent.onNext(checked);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName(), "", StoreContext.home.name());
  }

  private void showChipCancelButton(CheckBox chip) {
    Drawable cancelButton = getResources().getDrawable(
        themeManager.getAttributeForTheme(R.attr.cancelChipDrawable).resourceId);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      chip.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, cancelButton, null);
    } else {
      chip.setCompoundDrawablesWithIntrinsicBounds(null, null, cancelButton, null);
    }
  }

  private void hideChipCancelButton(CheckBox chip) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      chip.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
    } else {
      chip.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }
  }

  private void setupChipsListeners() {
    gamesChip.setOnCheckedChangeListener((__, isChecked) -> {
      if (isChecked) {
        showChipCancelButton(gamesChip);
      } else {
        hideChipCancelButton(gamesChip);
      }
    });

    appsChip.setOnCheckedChangeListener((__, isChecked) -> {
      if (isChecked) {
        showChipCancelButton(appsChip);
      } else {
        hideChipCancelButton(appsChip);
      }
    });
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_home_container, container, false);
  }

  @Override public void onDetach() {
    bottomNavigationActivity = null;
    super.onDetach();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    if (promotionsHomeDialog != null) {
      promotionsHomeDialog.destroyDialog();
      promotionsHomeDialog = null;
    }
    if (eskillsHomeDialog != null) {
      eskillsHomeDialog.destroyDialog();
      eskillsHomeDialog = null;
    }
    if (gdprDialog != null) {
      gdprDialog.destroyDialog();
      gdprDialog = null;
    }

    promotionsIcon = null;
    promotionsTicker = null;
    userAvatar = null;
    gamesChip = null;
    appsChip = null;
  }

  @Override public void setUserImage(String userAvatarUrl) {
    ImageLoader.with(getContext())
        .loadWithShadowCircleTransformWithPlaceholder(userAvatarUrl, userAvatar,
            R.drawable.ic_account_circle);
  }

  @Override public void setDefaultUserImage() {
    ImageLoader.with(getContext())
        .loadUsingCircleTransform(R.drawable.ic_account_circle, userAvatar);
  }

  @Override public void showAvatar() {
    userAvatar.setVisibility(View.VISIBLE);
  }

  @Override public Observable<Void> toolbarUserClick() {
    return RxView.clicks(userAvatar);
  }

  @Override public Observable<Void> toolbarPromotionsClick() {
    return RxView.clicks(promotionsIcon);
  }

  @Override public void showPromotionsHomeIcon(HomePromotionsWrapper homePromotionsWrapper) {
    promotionsIcon.setVisibility(View.VISIBLE);
    if (homePromotionsWrapper.getPromotions() > 0) {
      if (homePromotionsWrapper.getPromotions() < 10
          && homePromotionsWrapper.getTotalUnclaimedAppcValue() > 0) {
        promotionsTicker.setText(Integer.toString(homePromotionsWrapper.getPromotions()));
      } else {
        promotionsTicker.setText("9+");
      }
      promotionsTicker.setVisibility(View.VISIBLE);
    }
  }

  @Override public void showPromotionsHomeDialog(HomePromotionsWrapper homePromotionsWrapper) {
    promotionsHomeDialog.showDialog(homePromotionsWrapper);
  }

  @Override public void showEskillsHomeDialog() {
    eskillsHomeDialog.showDialog();
  }

  @Override public void hidePromotionsIcon() {
    promotionsIcon.setVisibility(View.GONE);
    promotionsTicker.setVisibility(View.GONE);
  }

  @Override public Observable<String> promotionsHomeDialogClicked() {
    return promotionsHomeDialog.dialogClicked();
  }

  @Override public Observable<String> eskillsHomeDialogClicked() {
    return eskillsHomeDialog.dialogClicked();
  }

  @Override public void dismissPromotionsDialog() {
    promotionsHomeDialog.dismissDialog();
  }

  @Override public void dismissEskillsDialog() {
    eskillsHomeDialog.dismissDialog();
  }

  @Override public void showTermsAndConditionsDialog() {
    gdprDialog.showDialog();
  }

  @Override public Observable<String> gdprDialogClicked() {
    return gdprDialog.dialogClicked();
  }

  @Override public Observable<Boolean> gamesChipClicked() {
    return RxView.clicks(gamesChip)
        .map(__ -> gamesChip.isChecked())
        .doOnNext(__ -> {
          if (appsChip.isChecked()) appsChip.setChecked(false);
        });
  }

  @Override public Observable<Boolean> appsChipClicked() {
    return RxView.clicks(appsChip)
        .map(__ -> appsChip.isChecked())
        .doOnNext(__ -> {
          if (gamesChip.isChecked()) gamesChip.setChecked(false);
        });
  }

  @Override public Observable<ChipsEvents> isChipChecked() {
    return chipCheckedEvent;
  }

  @Override public void uncheckChips() {
    gamesChip.setChecked(false);
    appsChip.setChecked(false);
  }

  @Override public void expandChips() {
    appBarLayout.setExpanded(true, true);
  }

  public enum ChipsEvents {
    GAMES, APPS, HOME,
  }
}
