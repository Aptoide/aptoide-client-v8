package cm.aptoide.pt.app.view;

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
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.DownloadAppViewModel;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.util.AppBarStateChangeListener;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.view.NotBottomNavigationView;
import cm.aptoide.pt.view.ThemeUtils;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import com.jakewharton.rxbinding.view.RxView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorNotImplementedException;
import rx.subjects.PublishSubject;

import static cm.aptoide.pt.utils.GenericDialogs.EResponse.YES;

/**
 * Created by D01 on 27/08/2018.
 */

public class EditorialFragment extends NavigationTrackFragment
    implements EditorialView, NotBottomNavigationView {

  @Inject EditorialPresenter presenter;
  private Toolbar toolbar;
  private ImageView appImage;
  private TextView itemName;
  private View appCardView;
  private View genericErrorView;
  private View noNetworkErrorView;
  private ProgressBar progressBar;
  private View genericRetryButton;
  private View noNetworkRetryButton;
  private RecyclerView editorialItems;
  private EditorialItemsAdapter adapter;
  private ImageView appCardImage;
  private TextView appCardTitle;
  private Button appCardButton;
  private View editorialItemsCard;
  private View actionItemCard;
  private LinearLayout downloadInfoLayout;
  private ProgressBar downloadProgressBar;
  private TextView downloadProgressValue;
  private ImageView cancelDownload;
  private ImageView pauseDownload;
  private ImageView resumeDownload;
  private View downloadControlsLayout;
  private RelativeLayout cardInfoLayout;

  private DownloadModel.Action action;
  private Subscription errorMessageSubscription;
  private PublishSubject<Void> ready;
  private CollapsingToolbarLayout collapsingToolbarLayout;
  private AppBarLayout appBarLayout;
  private TextView toolbarTitle;
  private Window window;
  private Drawable backArrow;
  private DecimalFormat oneDecimalFormatter;

  private PublishSubject<String> editorialMediaClicked;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    oneDecimalFormatter = new DecimalFormat("0.0");
    window = getActivity().getWindow();
    ready = PublishSubject.create();
    editorialMediaClicked = PublishSubject.create();
    setHasOptionsMenu(true);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window.setStatusBarColor(getResources().getColor(R.color.black_87_alpha));
    }

    toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    toolbar.setTitle("");
    AppCompatActivity appCompatActivity = ((AppCompatActivity) getActivity());
    appCompatActivity.setSupportActionBar(toolbar);
    ActionBar actionBar = appCompatActivity.getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
    backArrow = toolbar.getNavigationIcon();
    appBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar_layout);
    appImage = (ImageView) view.findViewById(R.id.app_graphic);
    itemName = (TextView) view.findViewById(R.id.action_item_name);
    appCardView = view.findViewById(R.id.app_cardview);
    appCardImage = (ImageView) appCardView.findViewById(R.id.app_icon_imageview);
    appCardTitle = (TextView) appCardView.findViewById(R.id.app_title_textview);
    appCardButton = (Button) appCardView.findViewById(R.id.appview_install_button);
    actionItemCard = view.findViewById(R.id.action_item_card);
    editorialItemsCard = view.findViewById(R.id.card_info_layout);
    editorialItems = (RecyclerView) view.findViewById(R.id.editorial_items);
    genericErrorView = view.findViewById(R.id.generic_error);
    noNetworkErrorView = view.findViewById(R.id.no_network_connection);
    progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
    genericRetryButton = genericErrorView.findViewById(R.id.retry);
    noNetworkRetryButton = noNetworkErrorView.findViewById(R.id.retry);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    adapter =
        new EditorialItemsAdapter(new ArrayList<>(), oneDecimalFormatter, editorialMediaClicked);
    editorialItems.setLayoutManager(linearLayoutManager);
    editorialItems.setAdapter(adapter);

    cardInfoLayout = (RelativeLayout) view.findViewById(R.id.card_info_install_layout);
    downloadControlsLayout = view.findViewById(R.id.install_controls_layout);
    downloadInfoLayout = ((LinearLayout) view.findViewById(R.id.appview_transfer_info));
    downloadProgressBar = ((ProgressBar) view.findViewById(R.id.appview_download_progress_bar));
    downloadProgressValue = (TextView) view.findViewById(R.id.appview_download_progress_number);
    cancelDownload = ((ImageView) view.findViewById(R.id.appview_download_cancel_button));
    resumeDownload = ((ImageView) view.findViewById(R.id.appview_download_resume_download));
    pauseDownload = ((ImageView) view.findViewById(R.id.appview_download_pause_download));
    toolbarTitle = ((TextView) view.findViewById(R.id.toolbar_title));
    collapsingToolbarLayout =
        ((CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar_layout));
    collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.tw__transparent));
    collapsingToolbarLayout.setCollapsedTitleTextColor(
        getResources().getColor(R.color.tw__transparent));

    appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {

      private void setExpandedState() {
        toolbar.setBackgroundDrawable(
            getResources().getDrawable(R.drawable.editorial_up_bottom_black_gradient));
        toolbarTitle.setTextColor(getResources().getColor(R.color.tw__solid_white));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          handleStatusBar(false);
        }
        if (backArrow != null) {
          backArrow.setColorFilter(getResources().getColor(R.color.tw__solid_white),
              PorterDuff.Mode.SRC_IN);
        }
      }

      private void setCollapsedState() {
        toolbar.setBackgroundColor(getResources().getColor(R.color.tw__transparent));
        toolbarTitle.setTextColor(getResources().getColor(R.color.black));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          handleStatusBar(true);
          //window.setStatusBarColor(getResources().getColor(R.color.white));
        }
        if (backArrow != null) {
          backArrow.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
        }
      }

      @Override public void onStateChanged(AppBarLayout appBarLayout, State state) {
        switch (state) {
          case EXPANDED:
            break;
          default:
          case IDLE:
          case MOVING:
            setExpandedState();
            break;
          case COLLAPSED:
            setCollapsedState();
            break;
        }
      }
    });

    editorialItems.setNestedScrollingEnabled(false);
    attachPresenter(presenter);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName(), "", StoreContext.home.name());
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (errorMessageSubscription != null && !errorMessageSubscription.isUnsubscribed()) {
      errorMessageSubscription.unsubscribe();
    }
    ready = null;
    window = null;
    oneDecimalFormatter = null;
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_empty, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void onDestroyView() {
    ThemeUtils.setStatusBarThemeColor(getActivity(), StoreTheme.get(getDefaultTheme()));
    window.getDecorView()
        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    toolbar = null;
    appImage = null;
    itemName = null;
    actionItemCard = null;
    appCardView = null;
    appCardImage = null;
    appCardTitle = null;
    appCardButton = null;

    editorialItemsCard = null;
    editorialItems = null;
    genericErrorView = null;
    noNetworkErrorView = null;
    progressBar = null;
    genericRetryButton = null;
    noNetworkRetryButton = null;
    collapsingToolbarLayout = null;
    appBarLayout = null;
    adapter = null;
    backArrow = null;

    super.onDestroyView();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.editorial_layout, container, false);
  }

  @Override public void showLoading() {
    actionItemCard.setVisibility(View.GONE);
    editorialItemsCard.setVisibility(View.GONE);
    appCardView.setVisibility(View.GONE);
    itemName.setVisibility(View.GONE);
    genericErrorView.setVisibility(View.GONE);
    noNetworkErrorView.setVisibility(View.GONE);
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    actionItemCard.setVisibility(View.GONE);
    editorialItemsCard.setVisibility(View.GONE);
    appCardView.setVisibility(View.GONE);
    itemName.setVisibility(View.GONE);
    genericErrorView.setVisibility(View.GONE);
    noNetworkErrorView.setVisibility(View.GONE);
    progressBar.setVisibility(View.GONE);
  }

  @Override public Observable<Void> retryClicked() {
    return Observable.merge(RxView.clicks(genericRetryButton), RxView.clicks(noNetworkRetryButton));
  }

  @Override public Observable<DownloadModel.Action> installButtonClick() {
    return RxView.clicks(appCardButton)
        .map(__ -> action);
  }

  @Override public Observable<Void> appCardClicked() {
    return RxView.clicks(appCardView);
  }

  @Override public void populateView(EditorialViewModel editorialViewModel) {
    populateAppContent(editorialViewModel);
    populateCardContent(editorialViewModel);
  }

  @Override public void showError(EditorialViewModel.Error error) {
    switch (error) {
      case NETWORK:
        noNetworkErrorView.setVisibility(View.VISIBLE);
        break;
      case GENERIC:
        genericErrorView.setVisibility(View.VISIBLE);
        break;
    }
  }

  @Override public void showDownloadAppModel(DownloadAppViewModel model) {
    DownloadModel downloadModel = model.getDownloadModel();
    this.action = downloadModel.getAction();
    if (downloadModel.isDownloading()) {
      downloadInfoLayout.setVisibility(View.VISIBLE);
      cardInfoLayout.setVisibility(View.GONE);
      setDownloadState(downloadModel.getProgress(), downloadModel.getDownloadState());
    } else {
      downloadInfoLayout.setVisibility(View.GONE);
      cardInfoLayout.setVisibility(View.VISIBLE);
      setButtonText(downloadModel);
      if (downloadModel.hasError()) {
        handleDownloadError(downloadModel.getDownloadState());
      }
    }
  }

  @Override public Observable<Boolean> showRootInstallWarningPopup() {
    return GenericDialogs.createGenericYesNoCancelMessage(this.getContext(), null,
        getResources().getString(R.string.root_access_dialog))
        .map(response -> (response.equals(YES)));
  }

  @Override public void openApp(String packageName) {
    AptoideUtils.SystemU.openApp(packageName, getContext().getPackageManager(), getContext());
  }

  @Override public Observable<Void> pauseDownload() {
    return RxView.clicks(pauseDownload);
  }

  @Override public Observable<Void> resumeDownload() {
    return RxView.clicks(resumeDownload);
  }

  @Override public Observable<Void> cancelDownload() {
    return RxView.clicks(cancelDownload);
  }

  @Override public Observable<Void> isAppViewReadyToDownload() {
    return ready;
  }

  @Override public void readyToDownload() {
    ready.onNext(null);
  }

  @Override public Observable<String> mediaContentClicked() {
    return editorialMediaClicked;
  }

  private void populateAppContent(EditorialViewModel editorialViewModel) {
    String title = editorialViewModel.getTitle();
    toolbar.setTitle(title);
    toolbarTitle.setText(title);
    actionItemCard.setVisibility(View.VISIBLE);
    if (editorialViewModel.hasBackgroundImage()) {
      ImageLoader.with(getContext())
          .load(editorialViewModel.getBackgroundImage(), appImage);
    } else {
      appImage.setBackgroundColor(getResources().getColor(R.color.grey_fog_normal));
    }
    appImage.setVisibility(View.VISIBLE);
    itemName.setText(editorialViewModel.getCardType());
    itemName.setVisibility(View.VISIBLE);
    appCardTitle.setText(editorialViewModel.getAppName());
    ImageLoader.with(getContext())
        .load(editorialViewModel.getIcon(), appCardImage);
    appCardView.setVisibility(View.VISIBLE);
  }

  private void populateCardContent(EditorialViewModel editorialViewModel) {
    if (editorialViewModel.hasContent()) {
      editorialItemsCard.setVisibility(View.VISIBLE);
      adapter.add(editorialViewModel.getContentList());
    }
  }

  private void setButtonText(DownloadModel model) {
    DownloadModel.Action action = model.getAction();
    switch (action) {
      case UPDATE:
        appCardButton.setText(getResources().getString(R.string.appview_button_update));
        break;
      case INSTALL:
        appCardButton.setText(getResources().getString(R.string.appview_button_install));
        break;
      case OPEN:
        appCardButton.setText(getResources().getString(R.string.appview_button_open));
        break;
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
        }, error -> new OnErrorNotImplementedException(error));
  }

  private void setDownloadState(int progress, DownloadModel.DownloadState downloadState) {

    LinearLayout.LayoutParams pauseShowing =
        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT, 4f);
    LinearLayout.LayoutParams pauseHidden =
        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT, 2f);
    switch (downloadState) {
      case ACTIVE:
        downloadProgressBar.setIndeterminate(false);
        downloadProgressBar.setProgress(progress);
        downloadProgressValue.setText(String.valueOf(progress) + "%");
        pauseDownload.setVisibility(View.VISIBLE);
        cancelDownload.setVisibility(View.GONE);
        resumeDownload.setVisibility(View.GONE);
        downloadControlsLayout.setLayoutParams(pauseShowing);
        break;
      case INDETERMINATE:
        downloadProgressBar.setIndeterminate(true);
        pauseDownload.setVisibility(View.VISIBLE);
        cancelDownload.setVisibility(View.GONE);
        resumeDownload.setVisibility(View.GONE);
        downloadControlsLayout.setLayoutParams(pauseShowing);
        break;
      case PAUSE:
        downloadProgressBar.setIndeterminate(false);
        downloadProgressBar.setProgress(progress);
        downloadProgressValue.setText(String.valueOf(progress) + "%");
        pauseDownload.setVisibility(View.GONE);
        cancelDownload.setVisibility(View.VISIBLE);
        resumeDownload.setVisibility(View.VISIBLE);
        downloadControlsLayout.setLayoutParams(pauseHidden);
        break;
      case COMPLETE:
        downloadProgressBar.setIndeterminate(true);
        pauseDownload.setVisibility(View.VISIBLE);
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
}
