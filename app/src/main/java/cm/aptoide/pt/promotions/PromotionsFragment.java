package cm.aptoide.pt.promotions;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.util.AppBarStateChangeListener;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.view.ThemeUtils;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorNotImplementedException;
import rx.subjects.PublishSubject;

import static cm.aptoide.pt.promotions.PromotionsAdapter.CLAIM;
import static cm.aptoide.pt.promotions.PromotionsAdapter.CLAIMED;
import static cm.aptoide.pt.promotions.PromotionsAdapter.DOWNGRADE;
import static cm.aptoide.pt.promotions.PromotionsAdapter.DOWNLOAD;
import static cm.aptoide.pt.promotions.PromotionsAdapter.DOWNLOADING;
import static cm.aptoide.pt.promotions.PromotionsAdapter.INSTALL;
import static cm.aptoide.pt.promotions.PromotionsAdapter.UPDATE;
import static cm.aptoide.pt.utils.GenericDialogs.EResponse.YES;

public class PromotionsFragment extends NavigationTrackFragment implements PromotionsView {
  private static final String WALLET_PACKAGE_NAME = "com.appcoins.wallet";

  @Inject PromotionsPresenter promotionsPresenter;
  private RecyclerView promotionsList;
  private PromotionsAdapter promotionsAdapter;
  private PublishSubject<PromotionAppClick> promotionAppClick;
  private TextView promotionFirstMessage;
  private View walletActiveView;
  private View walletInactiveView;
  private Button promotionAction;

  private Window window;
  private Toolbar toolbar;
  private Drawable backArrow;
  private AppBarLayout appBarLayout;
  private CollapsingToolbarLayout collapsingToolbarLayout;
  private TextView toolbarTitle;
  private Subscription errorMessageSubscription;
  private DecimalFormat decimalFormat;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    window = getActivity().getWindow();
    setHasOptionsMenu(true);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    decimalFormat = new DecimalFormat("0.0");
    promotionsList = view.findViewById(R.id.fragment_promotions_promotions_list);
    promotionAppClick = PublishSubject.create();
    promotionsAdapter = new PromotionsAdapter(new ArrayList<>(),
        new PromotionsViewHolderFactory(promotionAppClick, decimalFormat));

    promotionFirstMessage = view.findViewById(R.id.promotions_message_1);
    walletActiveView = view.findViewById(R.id.promotion_wallet_active);
    walletInactiveView = view.findViewById(R.id.promotion_wallet_inactive);
    promotionAction = walletInactiveView.findViewById(R.id.promotion_app_action_button);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window.setStatusBarColor(getResources().getColor(R.color.black_87_alpha));
    }
    toolbarTitle = view.findViewById(R.id.toolbar_title);
    toolbar = view.findViewById(R.id.toolbar);
    toolbar.setTitle("");
    AppCompatActivity appCompatActivity = ((AppCompatActivity) getActivity());
    appCompatActivity.setSupportActionBar(toolbar);
    ActionBar actionBar = appCompatActivity.getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
    backArrow = toolbar.getNavigationIcon();
    appBarLayout = view.findViewById(R.id.app_bar_layout);

    collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar_layout);
    collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.tw__transparent));
    collapsingToolbarLayout.setCollapsedTitleTextColor(
        getResources().getColor(R.color.tw__transparent));

    appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {

      private void configureAppBarLayout(Drawable drawable, int toolbarColor, boolean isCollapsed) {
        toolbar.setBackgroundDrawable(drawable);

        toolbarTitle.setTextColor(toolbarColor);
        toolbarTitle.setText(getString(R.string.holidayspromotion_title_holidays));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          handleStatusBar(isCollapsed);
        }
        if (backArrow != null) {
          backArrow.setColorFilter(toolbarColor, PorterDuff.Mode.SRC_IN);
        }
      }

      @Override public void onStateChanged(AppBarLayout appBarLayout, State state) {
        Resources resources = getResources();
        switch (state) {
          case EXPANDED:
            break;
          default:
          case IDLE:
          case MOVING:
            toolbarTitle.setVisibility(View.GONE);
            configureAppBarLayout(
                resources.getDrawable(R.drawable.editorial_up_bottom_black_gradient),
                resources.getColor(R.color.tw__solid_white), false);
            break;
          case COLLAPSED:
            toolbarTitle.setVisibility(View.VISIBLE);

            configureAppBarLayout(resources.getDrawable(R.drawable.tw__transparent),
                resources.getColor(R.color.black), true);
            break;
        }
      }
    });

    setupRecyclerView();
    attachPresenter(promotionsPresenter);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  private void handleStatusBar(boolean collapseState) {
    if (collapseState) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
          && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        window.setStatusBarColor(getResources().getColor(R.color.grey_medium));
      } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.getDecorView()
            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(getResources().getColor(R.color.white));
      }
    } else {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
          && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        window.setStatusBarColor(getResources().getColor(R.color.black_87_alpha));
        window.getDecorView()
            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
      } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.getDecorView()
            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        window.setStatusBarColor(getResources().getColor(R.color.black_87_alpha));
      }
    }
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
      if (promotionViewApp.getDownloadModel()
          .hasError()) {
        handleDownloadError(promotionViewApp.getDownloadModel()
            .getDownloadState());
      }
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

  @Override public void showAppCoinsAmount(int totalAppcValue) {
    promotionFirstMessage.setText(
        getString(R.string.holidayspromotion_message_1, String.valueOf(totalAppcValue)));
  }

  @Override public void lockPromotionApps(boolean walletInstalled) {
    promotionsAdapter.isWalletInstalled(walletInstalled);
  }

  @Override public Observable<PromotionViewApp> claimAppClick() {
    return promotionAppClick.filter(
        promotionAppClick -> promotionAppClick.getClickType() == PromotionAppClick.ClickType.CLAIM)
        .map(promotionAppClick -> promotionAppClick.getApp());
  }

  @Override public void updateClaimStatus(String packageName) {
    if (packageName.equals(WALLET_PACKAGE_NAME)) {
      promotionAction.setEnabled(false);
      promotionAction.setBackgroundColor(getContext().getResources()
          .getColor(R.color.grey_fog_light));
      promotionsAdapter.isWalletInstalled(true);
      promotionAction.setText(getContext().getString(R.string.holidayspromotion_button_claimed));
    } else {
      promotionsAdapter.updateClaimStatus(packageName);
    }
  }

  private void showWallet(PromotionViewApp promotionViewApp) {
    if (promotionViewApp.getDownloadModel()
        .isDownloading()) {
      walletActiveView.setVisibility(View.VISIBLE);
      walletInactiveView.setVisibility(View.GONE);
      ImageView appIcon = walletActiveView.findViewById(R.id.app_icon);
      TextView appName = walletActiveView.findViewById(R.id.app_name);
      TextView appDescription = walletActiveView.findViewById(R.id.app_description);
      TextView numberOfDownloads = walletActiveView.findViewById(R.id.number_of_downloads);
      TextView appSize = walletActiveView.findViewById(R.id.app_size);
      TextView rating = walletActiveView.findViewById(R.id.rating);
      ProgressBar downloadProgressBar =
          walletActiveView.findViewById(R.id.promotions_download_progress_bar);
      TextView downloadProgressValue =
          walletActiveView.findViewById(R.id.promotions_download_progress_number);
      ImageView pauseDownload =
          walletActiveView.findViewById(R.id.promotions_download_pause_download);
      ImageView cancelDownload =
          walletActiveView.findViewById(R.id.promotions_download_cancel_button);
      ImageView resumeDownload =
          walletActiveView.findViewById(R.id.promotions_download_resume_download);
      LinearLayout downloadControlsLayout =
          walletActiveView.findViewById(R.id.install_controls_layout);
      ImageLoader.with(getContext())
          .load(promotionViewApp.getAppIcon(), appIcon);
      appName.setText(promotionViewApp.getName());
      appDescription.setText(promotionViewApp.getDescription());
      appSize.setText(AptoideUtils.StringU.formatBytes(promotionViewApp.getSize(), false));
      DownloadModel.DownloadState downloadState = promotionViewApp.getDownloadModel()
          .getDownloadState();

      LinearLayout.LayoutParams pauseShowing =
          new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
              LinearLayout.LayoutParams.MATCH_PARENT, 4f);
      LinearLayout.LayoutParams pauseHidden =
          new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
              LinearLayout.LayoutParams.MATCH_PARENT, 2f);
      switch (downloadState) {
        case ACTIVE:
          downloadProgressBar.setIndeterminate(false);
          downloadProgressBar.setProgress(promotionViewApp.getDownloadModel()
              .getProgress());
          downloadProgressValue.setText(String.valueOf(promotionViewApp.getDownloadModel()
              .getProgress()) + "%");
          pauseDownload.setVisibility(View.VISIBLE);
          pauseDownload.setOnClickListener(__ -> promotionAppClick.onNext(
              new PromotionAppClick(promotionViewApp, PromotionAppClick.ClickType.PAUSE_DOWNLOAD)));
          cancelDownload.setVisibility(View.GONE);
          resumeDownload.setVisibility(View.GONE);
          downloadControlsLayout.setLayoutParams(pauseShowing);
          break;
        case INDETERMINATE:
          downloadProgressBar.setIndeterminate(true);
          pauseDownload.setVisibility(View.VISIBLE);
          pauseDownload.setOnClickListener(__ -> promotionAppClick.onNext(
              new PromotionAppClick(promotionViewApp, PromotionAppClick.ClickType.PAUSE_DOWNLOAD)));
          cancelDownload.setVisibility(View.GONE);
          resumeDownload.setVisibility(View.GONE);
          downloadControlsLayout.setLayoutParams(pauseShowing);
          break;
        case PAUSE:
          downloadProgressBar.setIndeterminate(false);
          downloadProgressBar.setProgress(promotionViewApp.getDownloadModel()
              .getProgress());
          downloadProgressValue.setText(String.valueOf(promotionViewApp.getDownloadModel()
              .getProgress()) + "%");
          pauseDownload.setVisibility(View.GONE);
          cancelDownload.setVisibility(View.VISIBLE);
          cancelDownload.setOnClickListener(__ -> promotionAppClick.onNext(
              new PromotionAppClick(promotionViewApp,
                  PromotionAppClick.ClickType.CANCEL_DOWNLOAD)));
          resumeDownload.setVisibility(View.VISIBLE);
          resumeDownload.setOnClickListener(__ -> promotionAppClick.onNext(
              new PromotionAppClick(promotionViewApp,
                  PromotionAppClick.ClickType.RESUME_DOWNLOAD)));
          downloadControlsLayout.setLayoutParams(pauseHidden);
          break;
        case COMPLETE:
          downloadProgressBar.setIndeterminate(true);
          pauseDownload.setVisibility(View.VISIBLE);
          pauseDownload.setOnClickListener(__ -> promotionAppClick.onNext(
              new PromotionAppClick(promotionViewApp, PromotionAppClick.ClickType.PAUSE_DOWNLOAD)));
          cancelDownload.setVisibility(View.GONE);
          resumeDownload.setVisibility(View.GONE);
          downloadControlsLayout.setLayoutParams(pauseShowing);
          break;
        case ERROR:
          showErrorDialog("", getContext().getString(R.string.error_occured));
          break;
        case NOT_ENOUGH_STORAGE_ERROR:
          showErrorDialog(getContext().getString(R.string.out_of_space_dialog_title),
              getContext().getString(R.string.out_of_space_dialog_message));
          break;
      }

      if (promotionViewApp.getRating() == 0) {
        rating.setText(R.string.appcardview_title_no_stars);
      } else {
        rating.setText(decimalFormat.format(promotionViewApp.getRating()));
      }
      numberOfDownloads.setText(String.valueOf(promotionViewApp.getNumberOfDownloads()));
    } else {
      if (promotionViewApp.getDownloadModel()
          .hasError()) {
        handleDownloadError(promotionViewApp.getDownloadModel()
            .getDownloadState());
      }

      walletActiveView.setVisibility(View.GONE);
      walletInactiveView.setVisibility(View.VISIBLE);
      ImageView appIcon = walletInactiveView.findViewById(R.id.app_icon);
      TextView appName = walletInactiveView.findViewById(R.id.app_name);
      TextView appDescription = walletInactiveView.findViewById(R.id.app_description);
      TextView numberOfDownloads = walletInactiveView.findViewById(R.id.number_of_downloads);
      TextView appSize = walletInactiveView.findViewById(R.id.app_size);
      TextView rating = walletInactiveView.findViewById(R.id.rating);

      ImageLoader.with(getContext())
          .load(promotionViewApp.getAppIcon(), appIcon);
      appName.setText(promotionViewApp.getName());
      appDescription.setText(promotionViewApp.getDescription());
      appSize.setText(AptoideUtils.StringU.formatBytes(promotionViewApp.getSize(), false));
      if (promotionViewApp.getRating() == 0) {
        rating.setText(R.string.appcardview_title_no_stars);
      } else {
        rating.setText(decimalFormat.format(promotionViewApp.getRating()));
      }
      numberOfDownloads.setText(String.valueOf(promotionViewApp.getNumberOfDownloads()));

      promotionAction.setText(getContext().getString(getButtonMessage(getState(promotionViewApp)),
          promotionViewApp.getAppcValue()));
      if (getState(promotionViewApp) == CLAIMED) {
        promotionAction.setEnabled(false);
        promotionAction.setBackgroundColor(getContext().getResources()
            .getColor(R.color.grey_fog_light));

        promotionsAdapter.isWalletInstalled(true);
      } else if (getState(promotionViewApp) == CLAIM) {
        promotionAction.setEnabled(true);
        promotionAction.setBackgroundColor(getContext().getResources()
            .getColor(R.color.green));
        promotionAction.setOnClickListener(__ -> promotionAppClick.onNext(
            new PromotionAppClick(promotionViewApp, getClickType(getState(promotionViewApp)))));
        promotionsAdapter.isWalletInstalled(true);
      } else {
        promotionAction.setEnabled(true);
        TypedValue resultValue = new TypedValue();
        getContext().getTheme()
            .resolveAttribute(R.attr.installButtonBackground, resultValue, true);
        if (resultValue.resourceId != 0) {
          promotionAction.setBackgroundResource(resultValue.resourceId);
        } else {
          promotionAction.setBackgroundColor(getContext().getResources()
              .getColor(R.color.orange));
        }
        promotionAction.setOnClickListener(__ -> promotionAppClick.onNext(
            new PromotionAppClick(promotionViewApp, getClickType(getState(promotionViewApp)))));
      }
    }
  }

  private void handleDownloadError(DownloadModel.DownloadState downloadState) {
    switch (downloadState) {
      case ERROR:
        showErrorDialog("", getContext().getString(R.string.error_occured));
        break;
      case NOT_ENOUGH_STORAGE_ERROR:
        showErrorDialog(getContext().getString(R.string.out_of_space_dialog_title),
            getContext().getString(R.string.out_of_space_dialog_message));
        break;
      default:
        throw new IllegalStateException("Invalid Download State " + downloadState);
    }
  }

  private void showErrorDialog(String title, String message) {
    errorMessageSubscription = GenericDialogs.createGenericOkMessage(getContext(), title, message)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(eResponse -> {
        }, error -> {
          throw new OnErrorNotImplementedException(error);
        });
  }

  private int getButtonMessage(int appState) {
    int message;
    switch (appState) {
      case DOWNGRADE:
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

  private int getState(PromotionViewApp app) {
    int state;
    if (app.isClaimed()) {
      return CLAIMED;
    } else {
      DownloadModel downloadModel = app.getDownloadModel();

      if (downloadModel.isDownloading()) {
        return DOWNLOADING;
      } else {
        switch (downloadModel.getAction()) {
          case DOWNGRADE:
            state = DOWNGRADE;
            break;
          case INSTALL:
            state = INSTALL;
            break;
          case OPEN:
            state = CLAIM;
            break;
          case UPDATE:
            state = UPDATE;
            break;
          default:
            throw new IllegalArgumentException("Invalid type of download action");
        }
        return state;
      }
    }
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
    ThemeUtils.setStatusBarThemeColor(getActivity(), StoreTheme.get(getDefaultTheme()));
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      window.getDecorView()
          .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }
    toolbarTitle = null;
    toolbar = null;
    promotionsList = null;
    promotionsAdapter = null;
    collapsingToolbarLayout = null;
    appBarLayout = null;
    backArrow = null;
    walletActiveView = null;
    walletInactiveView = null;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    window = null;
    promotionAppClick = null;
    if (errorMessageSubscription != null && !errorMessageSubscription.isUnsubscribed()) {
      errorMessageSubscription.unsubscribe();
    }
  }
}
