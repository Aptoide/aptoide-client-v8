package cm.aptoide.pt.promotions;

import android.app.AlertDialog;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.themes.ThemeManager;
import cm.aptoide.pt.util.AppBarStateChangeListener;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.jakewharton.rxbinding.view.RxView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorNotImplementedException;
import rx.subjects.PublishSubject;

import static cm.aptoide.pt.AptoideApplication.APPCOINS_WALLET_PACKAGE_NAME;
import static cm.aptoide.pt.promotions.PromotionsAdapter.CLAIM;
import static cm.aptoide.pt.promotions.PromotionsAdapter.CLAIMED;
import static cm.aptoide.pt.promotions.PromotionsAdapter.DOWNGRADE;
import static cm.aptoide.pt.promotions.PromotionsAdapter.DOWNLOAD;
import static cm.aptoide.pt.promotions.PromotionsAdapter.DOWNLOADING;
import static cm.aptoide.pt.promotions.PromotionsAdapter.INSTALL;
import static cm.aptoide.pt.promotions.PromotionsAdapter.UPDATE;
import static cm.aptoide.pt.utils.GenericDialogs.EResponse.YES;

public class PromotionsFragment extends NavigationTrackFragment implements PromotionsView {

  @Inject PromotionsPresenter promotionsPresenter;
  @Inject ThemeManager themeManager;
  private RecyclerView promotionsList;
  private PromotionsAdapter promotionsAdapter;
  private PublishSubject<PromotionAppClick> promotionAppClick;
  private PublishSubject<Void> promotionOverDialogClick;
  private TextView promotionTitle;
  private TextView promotionFirstMessage;
  private View walletActiveView;
  private View walletInactiveView;
  private View promotionsView;
  private ProgressBar loading;
  private Button promotionAction;
  private View genericErrorView;
  private View genericErrorViewRetry;
  private ImageView toolbarImage;
  private ImageView toolbarImagePlaceholder;

  private Window window;
  private Toolbar toolbar;
  private Drawable backArrow;
  private AppBarLayout appBarLayout;
  private CollapsingToolbarLayout collapsingToolbarLayout;
  private TextView toolbarTitle;
  private Subscription errorMessageSubscription;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    window = getActivity().getWindow();
    promotionOverDialogClick = PublishSubject.create();
    setHasOptionsMenu(true);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    promotionsList = view.findViewById(R.id.fragment_promotions_promotions_list);
    promotionAppClick = PublishSubject.create();
    promotionsAdapter = new PromotionsAdapter(new ArrayList<>(),
        new PromotionsViewHolderFactory(promotionAppClick, themeManager));

    toolbarImage = view.findViewById(R.id.app_graphic);
    toolbarImagePlaceholder = view.findViewById(R.id.app_graphic_placeholder);

    promotionTitle = view.findViewById(R.id.promotions_title);
    promotionFirstMessage = view.findViewById(R.id.promotions_message_1);
    walletActiveView = view.findViewById(R.id.promotion_wallet_active);
    walletInactiveView = view.findViewById(R.id.promotion_wallet_inactive);
    promotionAction = walletInactiveView.findViewById(R.id.promotion_app_action_button);
    loading = view.findViewById(R.id.progress_bar);
    promotionsView = view.findViewById(R.id.promotions_view);
    genericErrorView = view.findViewById(R.id.generic_error);
    genericErrorViewRetry = genericErrorView.findViewById(R.id.retry);

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
    collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.transparent));
    collapsingToolbarLayout.setCollapsedTitleTextColor(
        getResources().getColor(R.color.transparent));

    appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {

      private void configureAppBarLayout(Drawable drawable, int toolbarColor, boolean isCollapsed) {
        toolbar.setBackgroundDrawable(drawable);

        toolbarTitle.setTextColor(toolbarColor);
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
                resources.getColor(R.color.white), false);
            break;
          case COLLAPSED:
            toolbarTitle.setVisibility(View.VISIBLE);

            configureAppBarLayout(resources.getDrawable(R.drawable.transparent), resources.getColor(
                themeManager.getAttributeForTheme(R.attr.textColorBlackAlpha).resourceId), true);
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

  private void handleStatusBar(boolean isCollapsed) {
    if (isCollapsed) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && !themeManager.isThemeDark()) {
          window.getDecorView()
              .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        window.setStatusBarColor(getResources().getColor(
            themeManager.getAttributeForTheme(R.attr.statusBarColorSecondary).resourceId));
      }
    } else {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.setStatusBarColor(getResources().getColor(R.color.black_87_alpha));
        window.getDecorView()
            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
      }
    }
  }

  private void setupRecyclerView() {
    promotionsList.setAdapter(promotionsAdapter);
    promotionsList.setLayoutManager(
        new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
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

  @Override
  public void showPromotionApp(PromotionViewApp promotionViewApp, boolean isWalletInstalled) {
    if (promotionViewApp.getPackageName()
        .equals(APPCOINS_WALLET_PACKAGE_NAME)) {
      showWallet(promotionViewApp, isWalletInstalled);
      setWalletItemClickListener(promotionViewApp);
    } else {
      promotionsAdapter.setPromotionApp(promotionViewApp);
    }
    loading.setVisibility(View.GONE);
    toolbarImagePlaceholder.setVisibility(View.GONE);
    toolbarImage.setVisibility(View.VISIBLE);
    promotionsView.setVisibility(View.VISIBLE);
  }

  @Override public Observable<PromotionViewApp> installButtonClick() {
    return promotionAppClick.filter(
            promotionAppClick -> promotionAppClick.getClickType() == PromotionAppClick.ClickType.UPDATE
                || promotionAppClick.getClickType() == PromotionAppClick.ClickType.INSTALL_APP
                || promotionAppClick.getClickType() == PromotionAppClick.ClickType.DOWNLOAD
                || promotionAppClick.getClickType() == PromotionAppClick.ClickType.DOWNGRADE)
        .map(promotionAppClick -> promotionAppClick.getApp());
  }

  @Override public Observable<Boolean> showRootInstallWarningPopup() {
    return GenericDialogs.createGenericYesNoCancelMessage(this.getContext(), null,
            getResources().getString(R.string.root_access_dialog),
            themeManager.getAttributeForTheme(R.attr.dialogsTheme).resourceId)
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

  @Override public void setPromotionMessage(String message) {
    promotionFirstMessage.setText(message);
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
    if (packageName.equals(APPCOINS_WALLET_PACKAGE_NAME)) {
      setClaimedButton();
      promotionsAdapter.isWalletInstalled(true);
    } else {
      promotionsAdapter.updateClaimStatus(packageName);
    }
  }

  @Override public void showLoading() {
    toolbarImagePlaceholder.setVisibility(View.VISIBLE);
    toolbarImage.setVisibility(View.GONE);
    promotionsView.setVisibility(View.GONE);
    genericErrorView.setVisibility(View.GONE);
    loading.setVisibility(View.VISIBLE);
  }

  @Override public void showErrorView() {
    toolbarImage.setVisibility(View.GONE);
    loading.setVisibility(View.GONE);
    promotionsView.setVisibility(View.GONE);
    genericErrorView.setVisibility(View.VISIBLE);
  }

  @Override public Observable<Void> retryClicked() {
    return RxView.clicks(genericErrorViewRetry);
  }

  @Override public void showPromotionOverDialog() {
    loading.setVisibility(View.GONE);
    LayoutInflater inflater = LayoutInflater.from(getContext());
    View dialogLayout = inflater.inflate(R.layout.promotions_promotion_over_dialog, null);
    AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(dialogLayout)
        .create();
    dialog.setCancelable(false);

    dialogLayout.findViewById(R.id.dismiss_button)
        .setOnClickListener(view -> {
          promotionOverDialogClick.onNext(null);
          dialog.dismiss();
        });
    dialog.show();
  }

  @Override public Observable<Void> promotionOverDialogClick() {
    return promotionOverDialogClick;
  }

  @Override public void showPromotionTitle(String title) {
    promotionTitle.setText(title);
    toolbarTitle.setText(title);
  }

  @Override public void showPromotionFeatureGraphic(String background) {
    ImageLoader.with(getContext())
        .load(background, toolbarImage);
  }

  @Override public Observable<PromotionAppClick> appCardClick() {
    return promotionAppClick.filter(promotionAppClick -> promotionAppClick.getClickType()
        .equals(PromotionAppClick.ClickType.NAVIGATE));
  }

  @Override public void showDownloadError(PromotionViewApp promotionViewApp) {
    if (promotionViewApp.getDownloadModel()
        .hasError()) {
      handleDownloadError(promotionViewApp.getDownloadModel()
          .getDownloadState());
    }
  }

  private void setWalletItemClickListener(PromotionViewApp promotionViewApp) {
    View.OnClickListener listener = __ -> promotionAppClick.onNext(
        new PromotionAppClick(promotionViewApp, PromotionAppClick.ClickType.NAVIGATE));
    walletInactiveView.setOnClickListener(listener);
    walletActiveView.setOnClickListener(listener);
  }

  private void showWallet(PromotionViewApp promotionViewApp, boolean isWalletInstalled) {
    if (promotionViewApp.getDownloadModel()
        .isDownloading()) {
      walletActiveView.setVisibility(View.VISIBLE);
      walletInactiveView.setVisibility(View.GONE);
      ImageView appIcon = walletActiveView.findViewById(R.id.app_icon);
      TextView appName = walletActiveView.findViewById(R.id.app_name);
      TextView appRewardMessage = walletActiveView.findViewById(R.id.app_reward);
      TextView appDescription = walletActiveView.findViewById(R.id.app_description);
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
      appRewardMessage.setText(
          handleRewardMessage(promotionViewApp.getAppcValue(), promotionViewApp.getFiatSymbol(),
              promotionViewApp.getFiatValue(), promotionViewApp.getDownloadModel()
                  .getAction()
                  .equals(DownloadModel.Action.UPDATE)));
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
          downloadProgressValue.setText(promotionViewApp.getDownloadModel()
              .getProgress() + "%");
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
          downloadProgressValue.setText(promotionViewApp.getDownloadModel()
              .getProgress() + "%");
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
      }
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
      TextView appRewardMessage = walletInactiveView.findViewById(R.id.app_reward);

      ImageLoader.with(getContext())
          .load(promotionViewApp.getAppIcon(), appIcon);
      appName.setText(promotionViewApp.getName());
      appDescription.setText(promotionViewApp.getDescription());
      appRewardMessage.setText(
          handleRewardMessage(promotionViewApp.getAppcValue(), promotionViewApp.getFiatSymbol(),
              promotionViewApp.getFiatValue(), promotionViewApp.getDownloadModel()
                  .getAction()
                  .equals(DownloadModel.Action.UPDATE)));

      promotionAction.setText(getContext().getString(getButtonMessage(getState(promotionViewApp)),
          promotionViewApp.getAppcValue()));
      if (getState(promotionViewApp) == CLAIMED) {
        if (!isWalletInstalled()) {
          promotionAction.setEnabled(true);
          promotionAction.setBackgroundDrawable(getContext().getResources()
              .getDrawable(R.drawable.appc_gradient_rounded));
          promotionAction.setText(getContext().getString(R.string.appview_button_install));
          promotionAction.setOnClickListener(__ -> promotionAppClick.onNext(
              new PromotionAppClick(promotionViewApp, PromotionAppClick.ClickType.INSTALL_APP)));
        } else {

          setClaimedButton();
        }
        promotionsAdapter.isWalletInstalled(isWalletInstalled);
      } else if (getState(promotionViewApp) == CLAIM) {
        promotionAction.setEnabled(true);
        promotionAction.setBackgroundDrawable(getContext().getResources()
            .getDrawable(R.drawable.card_border_rounded_green));
        promotionAction.setTextColor(Color.WHITE);
        promotionAction.setOnClickListener(__ -> promotionAppClick.onNext(
            new PromotionAppClick(promotionViewApp, getClickType(getState(promotionViewApp)))));
        promotionsAdapter.isWalletInstalled(true);
      } else {
        promotionAction.setEnabled(true);
        promotionAction.setBackgroundDrawable(getContext().getResources()
            .getDrawable(R.drawable.appc_gradient_rounded));
        if (promotionViewApp.isClaimed()) {
          promotionAction.setText(getContext().getString(R.string.appview_button_install));
        }
        promotionAction.setOnClickListener(__ -> promotionAppClick.onNext(
            new PromotionAppClick(promotionViewApp, getClickType(getState(promotionViewApp)))));
      }
    }
  }

  private void setClaimedButton() {
    promotionAction.setEnabled(false);
    promotionAction.setBackgroundColor(getResources().getColor(R.color.grey_fog_light));
    promotionAction.setTextColor(getResources().getColor(R.color.black));

    SpannableString string = new SpannableString(
        "  " + getResources().getString(R.string.holidayspromotion_button_claimed)
            .toUpperCase());
    Drawable image =
        AppCompatResources.getDrawable(getContext(), R.drawable.ic_promotion_claimed_check);
    image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
    ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BASELINE);
    string.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    promotionAction.setTransformationMethod(null);
    promotionAction.setText(string);
  }

  private SpannableString handleRewardMessage(float appcValue, String fiatSymbol, double fiatValue,
      boolean isUpdate) {
    DecimalFormat fiatDecimalFormat = new DecimalFormat("0.00");
    String message = "";
    String appc = Double.toString(Math.floor(appcValue));
    if (isUpdate) {
      message = getString(R.string.FIATpromotion_update_to_get_short, appc,
          fiatSymbol + fiatDecimalFormat.format(fiatValue));
    } else {
      message = getString(R.string.FIATpromotion_install_to_get_short, appc,
          fiatSymbol + fiatDecimalFormat.format(fiatValue));
    }

    SpannableString spannable = new SpannableString(message);
    spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.promotions_reward)),
        message.indexOf(appc), message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    return spannable;
  }

  private boolean isWalletInstalled() {
    for (ApplicationInfo applicationInfo : getContext().getPackageManager()
        .getInstalledApplications(0)) {
      if (applicationInfo.packageName.equals(APPCOINS_WALLET_PACKAGE_NAME)) {
        return true;
      }
    }
    return false;
  }

  private void handleDownloadError(DownloadModel.DownloadState downloadState) {
    if (downloadState == DownloadModel.DownloadState.ERROR) {
      showErrorDialog("", getContext().getString(R.string.error_occured));
    }
  }

  private void showErrorDialog(String title, String message) {
    errorMessageSubscription = GenericDialogs.createGenericOkMessage(getContext(), title, message,
            themeManager.getAttributeForTheme(R.attr.dialogsTheme).resourceId)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(eResponse -> {
        }, error -> {
          throw new OnErrorNotImplementedException(error);
        });
  }

  private int getButtonMessage(int appState) {
    int message;
    switch (appState) {
      case UPDATE:
        message = R.string.appview_button_update;
        break;
      case DOWNGRADE:
      case DOWNLOAD:
      case INSTALL:
        message = R.string.install;
        break;
      case CLAIM:
        message = R.string.promotion_claim_button;
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
          if (app.isClaimed()) {
            return CLAIMED;
          }
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

  private PromotionAppClick.ClickType getClickType(int appState) {
    PromotionAppClick.ClickType clickType;
    switch (appState) {
      case DOWNGRADE:
        clickType = PromotionAppClick.ClickType.DOWNGRADE;
        break;
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
    themeManager.resetToBaseTheme();
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
    promotionOverDialogClick = null;
    if (errorMessageSubscription != null && !errorMessageSubscription.isUnsubscribed()) {
      errorMessageSubscription.unsubscribe();
    }
  }
}
