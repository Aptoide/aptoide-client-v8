package cm.aptoide.pt.home;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.promotions.PromotionsHomeDialog;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import com.jakewharton.rxbinding.view.RxView;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

public class HomeContainerFragment extends NavigationTrackFragment implements HomeContainerView {

  public static final String UP_SCROLL = "up";
  public static final String DOWN_SCROLL = "down";
  private static final BottomNavigationItem BOTTOM_NAVIGATION_ITEM = BottomNavigationItem.HOME;
  @Inject HomeContainerPresenter presenter;
  private BottomNavigationActivity bottomNavigationActivity;
  private CheckBox gamesChip;
  private CheckBox appsChip;
  private ImageView userAvatar;
  private ImageView promotionsIcon;
  private TextView promotionsTicker;
  private PromotionsHomeDialog promotionsHomeDialog;
  private LoggedInTermsAndConditionsDialog gdprDialog;

  private PublishSubject<String> chipCheckedEvent;

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
    gdprDialog = new LoggedInTermsAndConditionsDialog(getContext());
    if (bottomNavigationActivity != null) {
      bottomNavigationActivity.requestFocus(BOTTOM_NAVIGATION_ITEM);
    }
    gamesChip = view.findViewById(R.id.games_chip);
    appsChip = view.findViewById(R.id.apps_chip);

    gamesChip.setOnCheckedChangeListener((__, isChecked) -> {
      if (isChecked) {
        gamesChip.setTextColor(getResources().getColor(R.color.white));
      } else {
        gamesChip.setTextColor(getResources().getColor(R.color.default_orange_gradient_end));
      }
    });

    appsChip.setOnCheckedChangeListener((__, isChecked) -> {
      if (isChecked) {
        appsChip.setTextColor(getResources().getColor(R.color.white));
      } else {
        appsChip.setTextColor(getResources().getColor(R.color.default_orange_gradient_end));
      }
    });
    attachPresenter(presenter);
  }

  @Override public void onResume() {
    super.onResume();
    String checked = "";
    if (gamesChip.isChecked()) {
      checked = "games";
    } else if (appsChip.isChecked()) checked = "apps";
    chipCheckedEvent.onNext(checked);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName(), "", StoreContext.home.name());
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
      if (homePromotionsWrapper.getPromotions() < 10) {
        promotionsTicker.setText(Integer.toString(homePromotionsWrapper.getPromotions()));
      } else {
        promotionsTicker.setText("9+");
      }
      promotionsTicker.setVisibility(View.VISIBLE);
    }
  }

  @Override public void setPromotionsTickerWithValue(int promotions) {
    promotionsTicker.setText(Integer.toString(promotions));
    promotionsTicker.setVisibility(View.VISIBLE);
  }

  @Override public void setEllipsizedPromotionsTicker() {
    promotionsTicker.setText("9+");
    promotionsTicker.setVisibility(View.VISIBLE);
  }

  @Override public void showPromotionsHomeDialog(HomePromotionsWrapper homePromotionsWrapper) {
    promotionsHomeDialog.showDialog(getContext(), homePromotionsWrapper);
  }

  @Override public void hidePromotionsIcon() {
    promotionsIcon.setVisibility(View.GONE);
    promotionsTicker.setVisibility(View.GONE);
  }

  @Override public Observable<String> promotionsHomeDialogClicked() {
    return promotionsHomeDialog.dialogClicked();
  }

  @Override public void dismissPromotionsDialog() {
    promotionsHomeDialog.dismissDialog();
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

  @Override public Observable<String> isChipChecked() {
    return chipCheckedEvent;
  }
}
