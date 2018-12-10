package cm.aptoide.pt.promotions;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

import static cm.aptoide.pt.promotions.PromotionsAdapter.CLAIM;
import static cm.aptoide.pt.promotions.PromotionsAdapter.CLAIMED;
import static cm.aptoide.pt.promotions.PromotionsAdapter.DOWNLOAD;
import static cm.aptoide.pt.promotions.PromotionsAdapter.INSTALL;
import static cm.aptoide.pt.promotions.PromotionsAdapter.UPDATE;
import static cm.aptoide.pt.utils.GenericDialogs.EResponse.YES;

public class PromotionsFragment extends NavigationTrackFragment implements PromotionsView {

  @Inject PromotionsPresenter promotionsPresenter;
  private RecyclerView promotionsList;
  private PromotionsAdapter promotionsAdapter;
  private PublishSubject<PromotionAppClick> promotionAppClick;
  private TextView promotionFirstMessage;
  private View walletActiveView;
  private View walletInactiveView;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    promotionsList = view.findViewById(R.id.fragment_promotions_promotions_list);
    promotionAppClick = PublishSubject.create();
    promotionsAdapter = new PromotionsAdapter(new ArrayList<>(),
        new PromotionsViewHolderFactory(promotionAppClick));

    promotionFirstMessage = view.findViewById(R.id.promotions_message_1);
    walletActiveView = view.findViewById(R.id.promotion_wallet_active);
    walletInactiveView = view.findViewById(R.id.promotion_wallet_inactive);
    setupRecyclerView();
    attachPresenter(promotionsPresenter);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName(), "");
  }

  private void setupRecyclerView() {
    promotionsList.setAdapter(promotionsAdapter);
    promotionsList.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    RecyclerView.ItemAnimator animator = promotionsList.getItemAnimator();
    if (animator instanceof SimpleItemAnimator) {
      ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
    }
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_promotions, container, false);
  }

  @Override public void showPromotionApp(PromotionViewApp promotionViewApp) {
    if (promotionViewApp.getPackageName()
        .equals("com.appcoins.wallet")) {
      showWallet(promotionViewApp);
    } else {
      promotionsAdapter.setPromotionApp(promotionViewApp);
    }
  }

  @Override public Observable<PromotionViewApp> installButtonClick() {
    return promotionAppClick.filter(
        promotionAppClick -> promotionAppClick.getClickType() == PromotionAppClick.ClickType.UPDATE
            || promotionAppClick.getClickType() == PromotionAppClick.ClickType.INSTALL_APP
            || promotionAppClick.getClickType() == PromotionAppClick.ClickType.DOWNLOAD)
        .map(promotionAppClick -> promotionAppClick.getApp());
  }

  @Override public Observable<Boolean> showRootInstallWarningPopup() {
    return GenericDialogs.createGenericYesNoCancelMessage(this.getContext(), null,
        getResources().getString(R.string.root_access_dialog))
        .map(response -> (response.equals(YES)));
  }

  @Override public Observable<PromotionViewApp> pauseDownload() {
    return promotionAppClick.filter(promotionAppClick -> promotionAppClick.getClickType()
        == PromotionAppClick.ClickType.PAUSE_DOWNLOAD)
        .map(promotionAppClick -> promotionAppClick.getApp());
  }

  @Override public Observable<PromotionViewApp> cancelDownload() {
    return promotionAppClick.filter(promotionAppClick -> promotionAppClick.getClickType()
        == PromotionAppClick.ClickType.CANCEL_DOWNLOAD)
        .map(promotionAppClick -> promotionAppClick.getApp());
  }

  @Override public Observable<PromotionViewApp> resumeDownload() {
    return promotionAppClick.filter(promotionAppClick -> promotionAppClick.getClickType()
        == PromotionAppClick.ClickType.RESUME_DOWNLOAD)
        .map(promotionAppClick -> promotionAppClick.getApp());
  }

  @Override public void showAppCoinsAmmount(int totalAppcValue) {
    promotionFirstMessage.setText(
        getString(R.string.holidayspromotion_message_1, String.valueOf(totalAppcValue)));
  }

  private void showWallet(PromotionViewApp promotionViewApp) {
    if (promotionViewApp.getDownloadModel()
        .isDownloading()) {
      walletActiveView.setVisibility(View.VISIBLE);
      walletInactiveView.setVisibility(View.GONE);
    } else {
      walletActiveView.setVisibility(View.GONE);
      walletInactiveView.setVisibility(View.VISIBLE);
      ImageView appIcon = walletInactiveView.findViewById(R.id.app_icon);
      TextView appName = walletInactiveView.findViewById(R.id.app_name);
      TextView appDescription = walletInactiveView.findViewById(R.id.app_description);
      TextView numberOfDownloads = walletInactiveView.findViewById(R.id.number_of_downloads);
      TextView appSize = walletInactiveView.findViewById(R.id.app_size);
      TextView rating = walletInactiveView.findViewById(R.id.rating);
      Button promotionAction = walletInactiveView.findViewById(R.id.promotion_app_action_button);

      ImageLoader.with(getContext())
          .load(promotionViewApp.getAppIcon(), appIcon);
      appName.setText(promotionViewApp.getName());
      appDescription.setText(promotionViewApp.getDescription());
      appSize.setText(AptoideUtils.StringU.formatBytes(promotionViewApp.getSize(), false));
      if (promotionViewApp.getRating() == 0) {
        rating.setText(R.string.appcardview_title_no_stars);
      } else {
        rating.setText(new DecimalFormat("0.0").format(promotionViewApp.getRating()));
      }
      numberOfDownloads.setText(String.valueOf(promotionViewApp.getNumberOfDownloads()));

      promotionAction.setText(getContext().getString(getButtonMessage(getState(
          promotionViewApp.getDownloadModel()
              .getAction())), promotionViewApp.getAppcValue()));
      if (getState(promotionViewApp.getDownloadModel()
          .getAction()) == CLAIMED) {
        // TODO: 12/7/18 set button disabled state
      } else {
        promotionAction.setOnClickListener(__ -> promotionAppClick.onNext(
            new PromotionAppClick(promotionViewApp, getClickType(getState(
                promotionViewApp.getDownloadModel()
                    .getAction())))));
      }

      promotionAction.setOnClickListener(view -> promotionAppClick.onNext(
          new PromotionAppClick(promotionViewApp, getClickType(getState(
              promotionViewApp.getDownloadModel()
                  .getAction())))));
    }
  }

  private int getButtonMessage(int appState) {
    int message;
    switch (appState) {
      case UPDATE:
        message = R.string.holidayspromotion_button_update;
        break;
      case DOWNLOAD:
      case INSTALL:
        message = R.string.holidayspromotion_button_install;
        break;
      case CLAIM:
        message = R.string.holidayspromotion_button_claim;
        break;
      case CLAIMED:
        message = R.string.holidayspromotion_button_claimed;
        break;
      default:
        throw new IllegalArgumentException("Wrong view type of promotion app");
    }
    return message;
  }

  private int getState(DownloadModel.Action action) {
    return 3;
  }

  private PromotionAppClick.ClickType getClickType(int appState) {
    PromotionAppClick.ClickType clickType;
    switch (appState) {
      case UPDATE:
        clickType = PromotionAppClick.ClickType.UPDATE;
        break;
      case DOWNLOAD:
        clickType = PromotionAppClick.ClickType.DOWNLOAD;
        break;
      case INSTALL:
        clickType = PromotionAppClick.ClickType.INSTALL_APP;
        break;
      case CLAIM:
        clickType = PromotionAppClick.ClickType.CLAIM;
        break;
      default:
        throw new IllegalArgumentException("Wrong view type of promotion app");
    }
    return clickType;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    promotionsList = null;
    promotionsAdapter = null;
    walletActiveView = null;
    walletInactiveView = null;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    promotionAppClick = null;
  }
}
