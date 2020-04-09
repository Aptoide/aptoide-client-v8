package cm.aptoide.pt.editorial;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.aptoideviews.errors.ErrorView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.comments.refactor.CommentsView;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.editorial.epoxy.EditorialController;
import cm.aptoide.pt.editorial.epoxy.ReactionConfiguration;
import cm.aptoide.pt.editorial.epoxy.ReactionsModelPresenter;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.themes.ThemeManager;
import cm.aptoide.pt.util.AppBarStateChangeListener;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.view.NotBottomNavigationView;
import cm.aptoide.pt.view.Translator;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import com.airbnb.epoxy.EpoxyRecyclerView;
import com.airbnb.epoxy.EpoxyVisibilityTracker;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.jakewharton.rxbinding.view.RxView;
import java.text.DecimalFormat;
import javax.inject.Inject;
import javax.inject.Named;
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

  public static final String CARD_ID = "cardId";
  public static final String SLUG = "slug";
  public static final String FROM_HOME = "fromHome";
  private static final String TAG = EditorialFragment.class.getName();
  @Inject EditorialPresenter presenter;
  @Inject ReactionsModelPresenter reactionsModelPresenter;
  @Inject @Named("screenWidth") float screenWidth;
  @Inject @Named("screenHeight") float screenHeight;
  @Inject ThemeManager themeManager;
  @Inject CaptionBackgroundPainter captionBackgroundPainter;
  private Toolbar toolbar;
  private ImageView appImage;
  private TextView itemName;
  private View appCardView;
  private ErrorView errorView;
  private ProgressBar progressBar;
  private EpoxyRecyclerView editorialItems;
  private EditorialController editorialController;
  private ImageView appCardImage;
  private TextView appCardTitle;
  private Button appCardButton;
  private CardView actionItemCard;
  private LinearLayout downloadInfoLayout;
  private ProgressBar downloadProgressBar;
  private TextView downloadProgressValue;
  private ImageView cancelDownload;
  private ImageView pauseDownload;
  private ImageView resumeDownload;
  private View downloadControlsLayout;
  private RelativeLayout cardInfoLayout;
  private CommentsView commentsView;

  private DownloadModel.Action action;
  private Subscription errorMessageSubscription;
  private CollapsingToolbarLayout collapsingToolbarLayout;
  private AppBarLayout appBarLayout;
  private TextView toolbarTitle;
  private Window window;
  private Drawable backArrow;
  private DecimalFormat oneDecimalFormatter;
  private View appCardLayout;

  private PublishSubject<EditorialEvent> uiEventsListener;
  private PublishSubject<EditorialDownloadEvent> downloadEventListener;
  private PublishSubject<Void> snackListener;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    oneDecimalFormatter = new DecimalFormat("0.0");
    window = getActivity().getWindow();
    uiEventsListener = PublishSubject.create();
    downloadEventListener = PublishSubject.create();
    snackListener = PublishSubject.create();
    setHasOptionsMenu(true);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window.setStatusBarColor(getResources().getColor(R.color.black_87_alpha));
    }
    toolbar = view.findViewById(R.id.toolbar);
    toolbar.setTitle("");
    AppCompatActivity appCompatActivity = ((AppCompatActivity) getActivity());
    appCompatActivity.setSupportActionBar(toolbar);
    ActionBar actionBar = appCompatActivity.getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
    commentsView = view.findViewById(R.id.comments_view);
    backArrow = toolbar.getNavigationIcon();
    appBarLayout = view.findViewById(R.id.app_bar_layout);
    appImage = view.findViewById(R.id.app_graphic);
    itemName = view.findViewById(R.id.action_item_name);
    appCardLayout = view.findViewById(R.id.app_cardview_layout);
    appCardView = view.findViewById(R.id.app_cardview);
    appCardImage = appCardView.findViewById(R.id.app_icon_imageview);
    appCardTitle = appCardView.findViewById(R.id.app_title_textview);
    appCardButton = appCardView.findViewById(R.id.appview_install_button);
    actionItemCard = view.findViewById(R.id.action_item_card);
    editorialItems = view.findViewById(R.id.editorial_items);
    errorView = view.findViewById(R.id.error_view);
    progressBar = view.findViewById(R.id.progress_bar);

    EpoxyVisibilityTracker epoxyVisibilityTracker = new EpoxyVisibilityTracker();
    epoxyVisibilityTracker.attach(editorialItems);
    editorialController =
        new EditorialController(downloadEventListener, oneDecimalFormatter, reactionsModelPresenter,
            themeManager);
    editorialItems.setController(editorialController);

    cardInfoLayout = view.findViewById(R.id.card_info_install_layout);
    downloadControlsLayout = view.findViewById(R.id.install_controls_layout);
    downloadInfoLayout = view.findViewById(R.id.appview_transfer_info);
    downloadProgressBar = view.findViewById(R.id.appview_download_progress_bar);
    downloadProgressValue = view.findViewById(R.id.appview_download_progress_number);
    cancelDownload = view.findViewById(R.id.appview_download_cancel_button);
    resumeDownload = view.findViewById(R.id.appview_download_resume_download);
    pauseDownload = view.findViewById(R.id.appview_download_pause_download);
    toolbarTitle = view.findViewById(R.id.toolbar_title);
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
            configureAppBarLayout(
                resources.getDrawable(R.drawable.editorial_up_bottom_black_gradient),
                resources.getColor(R.color.white), false);
            break;
          case COLLAPSED:
            configureAppBarLayout(resources.getDrawable(
                themeManager.getAttributeForTheme(R.attr.toolbarBackgroundSecondary).resourceId),
                resources.getColor(
                    themeManager.getAttributeForTheme(R.attr.textColorBlackAlpha).resourceId),
                true);
            break;
        }
      }
    });
    attachPresenter(presenter);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName(), "", StoreContext.home.name());
  }

  @Override public void onDestroy() {
    uiEventsListener = null;
    snackListener = null;
    downloadEventListener = null;
    super.onDestroy();
    if (errorMessageSubscription != null && !errorMessageSubscription.isUnsubscribed()) {
      errorMessageSubscription.unsubscribe();
    }
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
    themeManager.resetStatusBarColor();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      window.getDecorView()
          .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }
    toolbar = null;
    appImage = null;
    itemName = null;
    actionItemCard = null;
    appCardView = null;
    appCardImage = null;
    appCardTitle = null;
    appCardButton = null;

    editorialItems = null;
    errorView = null;
    progressBar = null;
    collapsingToolbarLayout = null;
    appBarLayout = null;
    backArrow = null;

    cardInfoLayout = null;
    downloadControlsLayout = null;
    downloadInfoLayout = null;
    downloadProgressBar = null;
    downloadProgressValue = null;
    cancelDownload = null;
    resumeDownload = null;
    pauseDownload = null;
    appCardLayout = null;
    super.onDestroyView();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_editorial, container, false);
  }

  @Override public void showLoading() {
    actionItemCard.setVisibility(View.GONE);
    editorialItems.setVisibility(View.GONE);
    appCardView.setVisibility(View.GONE);
    itemName.setVisibility(View.GONE);
    errorView.setVisibility(View.GONE);
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    actionItemCard.setVisibility(View.GONE);
    editorialItems.setVisibility(View.GONE);
    appCardView.setVisibility(View.GONE);
    itemName.setVisibility(View.GONE);
    errorView.setVisibility(View.GONE);
    progressBar.setVisibility(View.GONE);
  }

  @Override public Observable<Void> retryClicked() {
    return errorView.retryClick();
  }

  @Override public Observable<EditorialEvent> appCardClicked(EditorialViewModel model) {
    return RxView.clicks(appCardView)
        .map(click -> new EditorialEvent(EditorialEvent.Type.APPCARD, model.getBottomCardAppModel()
            .getId(), model.getBottomCardAppModel()
            .getPackageName()))
        .mergeWith(uiEventsListener.filter(editorialEvent -> editorialEvent.getClickType()
            .equals(EditorialEvent.Type.APPCARD)));
  }

  @Override public Observable<EditorialEvent> actionButtonClicked() {
    return uiEventsListener.filter(editorialEvent -> editorialEvent.getClickType()
        .equals(EditorialEvent.Type.ACTION));
  }

  @Override public void showError(EditorialViewModel.Error error) {
    switch (error) {
      case NETWORK:
        errorView.setError(ErrorView.Error.NO_NETWORK);
        errorView.setVisibility(View.VISIBLE);
        break;
      case GENERIC:
        errorView.setError(ErrorView.Error.GENERIC);
        errorView.setVisibility(View.VISIBLE);
        break;
    }
  }

  @Override public Observable<Boolean> showRootInstallWarningPopup() {
    return GenericDialogs.createGenericYesNoCancelMessage(this.getContext(), null,
        getResources().getString(R.string.root_access_dialog),
        themeManager.getAttributeForTheme(R.attr.dialogsTheme).resourceId)
        .map(response -> (response.equals(YES)));
  }

  @Override public void openApp(String packageName) {
    AptoideUtils.SystemU.openApp(packageName, getContext().getPackageManager(), getContext());
  }

  @Override public Observable<EditorialDownloadEvent> installButtonClick(
      EditorialViewModel editorialViewModel) {
    return RxView.clicks(appCardButton)
        .map(__ -> new EditorialDownloadEvent(editorialViewModel, action))
        .mergeWith(downloadEventListener.filter(editorialEvent -> editorialEvent.getClickType()
            .equals(EditorialEvent.Type.BUTTON)));
  }

  @Override
  public Observable<EditorialDownloadEvent> pauseDownload(EditorialViewModel editorialViewModel) {
    return RxView.clicks(pauseDownload)
        .map(click -> new EditorialDownloadEvent(EditorialEvent.Type.PAUSE,
            editorialViewModel.getBottomCardAppModel()
                .getPackageName(), editorialViewModel.getBottomCardAppModel()
            .getMd5sum(), editorialViewModel.getBottomCardAppModel()
            .getVerCode(), editorialViewModel.getBottomCardAppModel()
            .getId()))
        .mergeWith(downloadEventListener.filter(editorialEvent -> editorialEvent.getClickType()
            .equals(EditorialEvent.Type.PAUSE)));
  }

  @Override
  public Observable<EditorialDownloadEvent> resumeDownload(EditorialViewModel editorialViewModel) {
    return RxView.clicks(resumeDownload)
        .map(click -> new EditorialDownloadEvent(EditorialEvent.Type.RESUME,
            editorialViewModel.getBottomCardAppModel()
                .getPackageName(), editorialViewModel.getBottomCardAppModel()
            .getMd5sum(), editorialViewModel.getBottomCardAppModel()
            .getVerCode(), editorialViewModel.getBottomCardAppModel()
            .getId(), action))
        .mergeWith(downloadEventListener.filter(editorialEvent -> editorialEvent.getClickType()
            .equals(EditorialEvent.Type.RESUME)));
  }

  @Override
  public Observable<EditorialDownloadEvent> cancelDownload(EditorialViewModel editorialViewModel) {
    return RxView.clicks(cancelDownload)
        .map(click -> new EditorialDownloadEvent(EditorialEvent.Type.CANCEL,
            editorialViewModel.getBottomCardAppModel()
                .getPackageName(), editorialViewModel.getBottomCardAppModel()
            .getMd5sum(), editorialViewModel.getBottomCardAppModel()
            .getVerCode(), editorialViewModel.getBottomCardAppModel()
            .getId()))
        .mergeWith(downloadEventListener.filter(editorialEvent -> editorialEvent.getClickType()
            .equals(EditorialEvent.Type.CANCEL)));
  }

  @Override public Observable<Boolean> bottomCardVisibilityChange() {
    return editorialController.getBottomCardVisibilityChange();
  }

  @Override public void removeBottomCardAnimation() {
    configureAppCardAnimation(appCardLayout, false);
  }

  @Override public void addBottomCardAnimation() {
    configureAppCardAnimation(appCardLayout, true);
  }

  @Override public Observable<EditorialEvent> mediaContentClicked() {
    return uiEventsListener.filter(editorialEvent -> editorialEvent.getClickType()
        .equals(EditorialEvent.Type.MEDIA));
  }

  @Override public Observable<EditorialEvent> mediaListDescriptionChanged() {
    return uiEventsListener.filter(editorialEvent -> editorialEvent.getClickType()
        .equals(EditorialEvent.Type.MEDIA_LIST))
        .distinctUntilChanged(EditorialEvent::getFirstVisiblePosition);
  }

  @Override public Observable<Boolean> showDowngradeMessage() {
    return GenericDialogs.createGenericContinueCancelMessage(getContext(), null,
        getContext().getResources()
            .getString(R.string.downgrade_warning_dialog),
        themeManager.getAttributeForTheme(R.attr.dialogsTheme).resourceId)
        .map(eResponse -> eResponse.equals(YES));
  }

  @Override public Observable<Void> snackLoginClick() {
    return snackListener;
  }

  @Override public CommentsView getCommentsView() {
    return commentsView;
  }

  @Override public void populateView(EditorialViewModel editorialViewModel) {
    populateAppContent(editorialViewModel);
  }

  private void populateAppContent(EditorialViewModel editorialViewModel) {
    if (editorialViewModel.hasBackgroundImage()) {
      ImageLoader.with(getContext())
          .load(editorialViewModel.getBackgroundImage(), appImage);
    } else {
      appImage.setBackgroundColor(getResources().getColor(R.color.grey_fog_normal));
    }
    String caption = editorialViewModel.getCaption();
    toolbar.setTitle(caption);
    toolbarTitle.setText(editorialViewModel.getTitle());
    appImage.setVisibility(View.VISIBLE);
    itemName.setText(Translator.translate(caption, getContext(), ""));
    captionBackgroundPainter.addColorBackgroundToCaption(actionItemCard,
        editorialViewModel.getCaptionColor());
    itemName.setVisibility(View.VISIBLE);
    actionItemCard.setVisibility(View.VISIBLE);
  }

  private void setBottomAppCardInfo(EditorialViewModel editorialViewModel) {
    if (editorialViewModel.shouldHaveAnimation()) {
      appCardTitle.setText(editorialViewModel.getBottomCardAppModel()
          .getName());
      appCardTitle.setVisibility(View.VISIBLE);
      ImageLoader.with(getContext())
          .load(editorialViewModel.getBottomCardAppModel()
              .getIcon(), appCardImage);
      appCardView.setVisibility(View.VISIBLE);

      EditorialDownloadModel downloadModel = editorialViewModel.getPlaceHolderContent()
          .get(0)
          .getApp()
          .getDownloadModel();
      if (downloadModel != null) {
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
    }
  }

  @Override public void populateCardContent(EditorialViewModel editorialViewModel) {
    if (editorialViewModel.hasContent()) {
      editorialItems.setVisibility(View.VISIBLE);
      editorialController.setData(editorialViewModel.getContentList(),
          editorialViewModel.shouldHaveAnimation(),
          new ReactionConfiguration(editorialViewModel.getCardId(), editorialViewModel.getGroupId(),
              ReactionConfiguration.ReactionSource.CURATION_DETAIL));
    }
    setBottomAppCardInfo(editorialViewModel);
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
      case DOWNGRADE:
        appCardButton.setText(getResources().getString(R.string.appview_button_downgrade));
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
    errorMessageSubscription = GenericDialogs.createGenericOkMessage(getContext(), title, message,
        themeManager.getAttributeForTheme(R.attr.dialogsTheme).resourceId)
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
        downloadProgressValue.setText(progress + "%");
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
        downloadProgressValue.setText(progress + "%");
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

  private void configureAppCardAnimation(View layout, boolean show) {
    if (show) {
      Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
      animation.setFillAfter(true);
      layout.startAnimation(animation);
    } else {
      Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);
      animation.setFillAfter(true);
      layout.startAnimation(animation);
    }
  }
}
