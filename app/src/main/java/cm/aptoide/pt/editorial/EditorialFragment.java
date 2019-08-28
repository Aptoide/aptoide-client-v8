package cm.aptoide.pt.editorial;

import android.animation.Animator;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
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
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.aptoideviews.errors.ErrorView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.reactions.ReactionEvent;
import cm.aptoide.pt.reactions.TopReactionsPreview;
import cm.aptoide.pt.reactions.data.TopReaction;
import cm.aptoide.pt.reactions.ui.ReactionsPopup;
import cm.aptoide.pt.util.AppBarStateChangeListener;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.view.NotBottomNavigationView;
import cm.aptoide.pt.view.ThemeUtils;
import cm.aptoide.pt.view.Translator;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.jakewharton.rxbinding.support.v4.widget.RxNestedScrollView;
import com.jakewharton.rxbinding.view.RxView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorNotImplementedException;
import rx.subjects.PublishSubject;

import static cm.aptoide.pt.reactions.ReactionMapper.mapReaction;
import static cm.aptoide.pt.reactions.ReactionMapper.mapUserReaction;
import static cm.aptoide.pt.utils.GenericDialogs.EResponse.YES;

/**
 * Created by D01 on 27/08/2018.
 */

public class EditorialFragment extends NavigationTrackFragment
    implements EditorialView, NotBottomNavigationView {

  public static final String CARD_ID = "cardId";
  public static final String FROM_HOME = "fromHome";
  private static final String TAG = EditorialFragment.class.getName();
  @Inject EditorialPresenter presenter;
  @Inject @Named("screenWidth") float screenWidth;
  @Inject @Named("screenHeight") float screenHeight;
  @Inject @Named("aptoide-theme") String theme;
  @Inject CaptionBackgroundPainter captionBackgroundPainter;
  private Toolbar toolbar;
  private ImageView appImage;
  private TextView itemName;
  private View appCardView;
  private ErrorView errorView;
  private ProgressBar progressBar;
  private RecyclerView editorialItems;
  private EditorialItemsAdapter adapter;
  private ImageView appCardImage;
  private TextView appCardTitle;
  private Button appCardButton;
  private View editorialItemsCard;
  private CardView actionItemCard;
  private LinearLayout downloadInfoLayout;
  private ProgressBar downloadProgressBar;
  private TextView downloadProgressValue;
  private ImageView cancelDownload;
  private ImageView pauseDownload;
  private ImageView resumeDownload;
  private View downloadControlsLayout;
  private RelativeLayout cardInfoLayout;
  private ImageButton reactButton;

  private DownloadModel.Action action;
  private Subscription errorMessageSubscription;
  private PublishSubject<Void> ready;
  private CollapsingToolbarLayout collapsingToolbarLayout;
  private AppBarLayout appBarLayout;
  private TextView toolbarTitle;
  private Window window;
  private Drawable backArrow;
  private DecimalFormat oneDecimalFormatter;
  private NestedScrollView scrollView;
  private View appCardLayout;
  private List<Integer> placeHolderPositions;

  private PublishSubject<EditorialEvent> uiEventsListener;
  private PublishSubject<EditorialDownloadEvent> downloadEventListener;
  private PublishSubject<ReactionEvent> reactionEventListener;
  private PublishSubject<Void> snackListener;
  private PublishSubject<Boolean> movingCollapseSubject;
  private TopReactionsPreview topReactionsPreview;
  private boolean shouldAnimate;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    oneDecimalFormatter = new DecimalFormat("0.0");
    window = getActivity().getWindow();
    ready = PublishSubject.create();
    uiEventsListener = PublishSubject.create();
    downloadEventListener = PublishSubject.create();
    movingCollapseSubject = PublishSubject.create();
    reactionEventListener = PublishSubject.create();
    snackListener = PublishSubject.create();
    topReactionsPreview = new TopReactionsPreview();
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
    scrollView = (NestedScrollView) view.findViewById(R.id.nested_scroll_view);
    appBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar_layout);
    appImage = (ImageView) view.findViewById(R.id.app_graphic);
    itemName = (TextView) view.findViewById(R.id.action_item_name);
    appCardLayout = view.findViewById(R.id.app_cardview_layout);
    appCardView = view.findViewById(R.id.app_cardview);
    appCardImage = (ImageView) appCardView.findViewById(R.id.app_icon_imageview);
    appCardTitle = (TextView) appCardView.findViewById(R.id.app_title_textview);
    appCardButton = (Button) appCardView.findViewById(R.id.appview_install_button);
    actionItemCard = view.findViewById(R.id.action_item_card);
    editorialItemsCard = view.findViewById(R.id.card_info_layout);
    editorialItems = (RecyclerView) view.findViewById(R.id.editorial_items);
    errorView = view.findViewById(R.id.error_view);
    progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    layoutManager.setOrientation(RecyclerView.VERTICAL);
    adapter = new EditorialItemsAdapter(new ArrayList<>(), oneDecimalFormatter, uiEventsListener,
        downloadEventListener);
    editorialItems.setLayoutManager(layoutManager);
    editorialItems.setAdapter(adapter);

    reactButton = view.findViewById(R.id.add_reactions);
    topReactionsPreview.initialReactionsSetup(view);

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
            movingCollapseSubject.onNext(isItemShown());
            break;
          default:
          case IDLE:
          case MOVING:
            movingCollapseSubject.onNext(isItemShown());
            configureAppBarLayout(
                resources.getDrawable(R.drawable.editorial_up_bottom_black_gradient),
                resources.getColor(R.color.white), false);
            break;
          case COLLAPSED:
            movingCollapseSubject.onNext(isItemShown());
            configureAppBarLayout(resources.getDrawable(R.drawable.transparent),
                resources.getColor(R.color.black), true);
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
    reactionEventListener = null;
    downloadEventListener = null;
    super.onDestroy();
    if (errorMessageSubscription != null && !errorMessageSubscription.isUnsubscribed()) {
      errorMessageSubscription.unsubscribe();
    }
    ready = null;
    window = null;
    oneDecimalFormatter = null;
    movingCollapseSubject = null;
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
    ThemeUtils.setStatusBarThemeColor(getActivity(), theme);
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

    editorialItemsCard = null;
    editorialItems = null;
    errorView = null;
    progressBar = null;
    collapsingToolbarLayout = null;
    appBarLayout = null;
    adapter = null;
    backArrow = null;

    reactButton = null;

    cardInfoLayout = null;
    downloadControlsLayout = null;
    downloadInfoLayout = null;
    downloadProgressBar = null;
    downloadProgressValue = null;
    cancelDownload = null;
    resumeDownload = null;
    pauseDownload = null;
    scrollView = null;
    appCardLayout = null;
    topReactionsPreview.onDestroy();
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
    errorView.setVisibility(View.GONE);
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    actionItemCard.setVisibility(View.GONE);
    editorialItemsCard.setVisibility(View.GONE);
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
        .map(click -> new EditorialEvent(EditorialEvent.Type.APPCARD, model.getBottomCardAppId(),
            model.getBottomCardPackageName()))
        .mergeWith(uiEventsListener.filter(editorialEvent -> editorialEvent.getClickType()
            .equals(EditorialEvent.Type.APPCARD)));
  }

  @Override public Observable<EditorialEvent> actionButtonClicked() {
    return uiEventsListener.filter(editorialEvent -> editorialEvent.getClickType()
        .equals(EditorialEvent.Type.ACTION));
  }

  @Override public void populateView(EditorialViewModel editorialViewModel) {
    populateAppContent(editorialViewModel);
    populateCardContent(editorialViewModel);
    ready.onNext(null);
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

  @Override public void showDownloadModel(EditorialDownloadModel downloadModel) {
    this.action = downloadModel.getAction();
    EditorialItemsViewHolder placeHolderViewHolder =
        getViewHolderForAdapterPosition(downloadModel.getPosition());
    if (downloadModel.isDownloading()) {
      downloadInfoLayout.setVisibility(View.VISIBLE);
      cardInfoLayout.setVisibility(View.GONE);
      setDownloadState(downloadModel.getProgress(), downloadModel.getDownloadState());
      if (placeHolderViewHolder != null) {
        placeHolderViewHolder.setPlaceHolderDownloadingInfo(downloadModel);
      }
    } else {
      downloadInfoLayout.setVisibility(View.GONE);
      cardInfoLayout.setVisibility(View.VISIBLE);
      setButtonText(downloadModel);
      if (placeHolderViewHolder != null) {
        placeHolderViewHolder.setPlaceHolderDefaultStateInfo(downloadModel,
            getResources().getString(R.string.appview_button_update),
            getResources().getString(R.string.appview_button_install),
            getResources().getString(R.string.appview_button_open),
            getResources().getString(R.string.appview_button_downgrade));
      }
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
            editorialViewModel.getBottomCardPackageName(), editorialViewModel.getBottomCardMd5(),
            editorialViewModel.getBottomCardVersionCode(), editorialViewModel.getBottomCardAppId()))
        .mergeWith(downloadEventListener.filter(editorialEvent -> editorialEvent.getClickType()
            .equals(EditorialEvent.Type.PAUSE)));
  }

  @Override
  public Observable<EditorialDownloadEvent> resumeDownload(EditorialViewModel editorialViewModel) {
    return RxView.clicks(resumeDownload)
        .map(click -> new EditorialDownloadEvent(EditorialEvent.Type.RESUME,
            editorialViewModel.getBottomCardPackageName(), editorialViewModel.getBottomCardMd5(),
            editorialViewModel.getBottomCardVersionCode(), editorialViewModel.getBottomCardAppId()))
        .mergeWith(downloadEventListener.filter(editorialEvent -> editorialEvent.getClickType()
            .equals(EditorialEvent.Type.RESUME)));
  }

  @Override
  public Observable<EditorialDownloadEvent> cancelDownload(EditorialViewModel editorialViewModel) {
    return RxView.clicks(cancelDownload)
        .map(click -> new EditorialDownloadEvent(EditorialEvent.Type.CANCEL,
            editorialViewModel.getBottomCardPackageName(), editorialViewModel.getBottomCardMd5(),
            editorialViewModel.getBottomCardVersionCode(), editorialViewModel.getBottomCardAppId()))
        .mergeWith(downloadEventListener.filter(editorialEvent -> editorialEvent.getClickType()
            .equals(EditorialEvent.Type.CANCEL)));
  }

  @Override public Observable<Void> isViewReady() {
    return ready;
  }

  @Override public Observable<ScrollEvent> placeHolderVisibilityChange() {
    return RxNestedScrollView.scrollChangeEvents(scrollView)
        .flatMap(viewScrollChangeEvent -> Observable.just(viewScrollChangeEvent)
            .map(scrollDown -> isItemShown())
            .map(isItemShown -> new ScrollEvent(
                isScrollDown(viewScrollChangeEvent.oldScrollY(), viewScrollChangeEvent.scrollY()),
                isItemShown)))
        .distinctUntilChanged(ScrollEvent::getItemShown);
  }

  @Override public void removeBottomCardAnimation() {
    if (placeHolderPositions != null && !placeHolderPositions.isEmpty()) {
      EditorialItemsViewHolder placeHolderViewHolder =
          getViewHolderForAdapterPosition(placeHolderPositions.get(0));
      if (placeHolderViewHolder != null) {
        View view = placeHolderViewHolder.getPlaceHolder();
        if (view != null && shouldAnimate) {
          configureAppCardAnimation(appCardLayout, view, 0f, 300, true);
        }
      }
    }
  }

  @Override public void addBottomCardAnimation() {
    if (placeHolderPositions != null && !placeHolderPositions.isEmpty()) {
      EditorialItemsViewHolder placeHolderViewHolder =
          getViewHolderForAdapterPosition(placeHolderPositions.get(0));
      if (placeHolderViewHolder != null) {
        View view = placeHolderViewHolder.getPlaceHolder();
        if (view != null && shouldAnimate) {
          configureAppCardAnimation(view, appCardLayout, 0.1f, 300, false);
        }
      }
    }
  }

  @Override public Observable<EditorialEvent> mediaContentClicked() {
    return uiEventsListener.filter(editorialEvent -> editorialEvent.getClickType()
        .equals(EditorialEvent.Type.MEDIA));
  }

  @Override public void managePlaceHolderVisibity() {
    if (placeHolderPositions != null && !placeHolderPositions.isEmpty()) {
      EditorialItemsViewHolder placeHolderViewHolder =
          getViewHolderForAdapterPosition(placeHolderPositions.get(0));
      if (placeHolderViewHolder != null && placeHolderViewHolder.isVisible(screenHeight,
          screenWidth)) {
        removeBottomCardAnimation();
      }
    }
  }

  @Override public Observable<EditorialEvent> mediaListDescriptionChanged() {
    return uiEventsListener.filter(editorialEvent -> editorialEvent.getClickType()
        .equals(EditorialEvent.Type.MEDIA_LIST))
        .distinctUntilChanged(EditorialEvent::getFirstVisiblePosition);
  }

  @Override
  public void manageMediaListDescriptionAnimationVisibility(EditorialEvent editorialEvent) {
    int contentPosition = editorialEvent.getPosition();
    EditorialItemsViewHolder editorialItemsViewHolder =
        ((EditorialItemsViewHolder) editorialItems.findViewHolderForAdapterPosition(
            contentPosition));
    if (editorialItemsViewHolder != null) {
      editorialItemsViewHolder.manageDescriptionAnimationVisibility(
          editorialEvent.getFirstVisiblePosition(), editorialEvent.getMedia());
    }
  }

  @Override public void setMediaListDescriptionsVisible(EditorialEvent editorialEvent) {
    EditorialItemsViewHolder editorialItemsViewHolder =
        ((EditorialItemsViewHolder) editorialItems.findViewHolderForAdapterPosition(
            editorialEvent.getPosition()));
    if (editorialItemsViewHolder != null) {
      editorialItemsViewHolder.setAllDescriptionsVisible();
    }
  }

  @Override public Observable<Boolean> handleMovingCollapse() {
    return movingCollapseSubject.distinctUntilChanged();
  }

  @Override public Observable<Boolean> showDowngradeMessage() {
    return GenericDialogs.createGenericContinueCancelMessage(getContext(), null,
        getContext().getResources()
            .getString(R.string.downgrade_warning_dialog))
        .map(eResponse -> eResponse.equals(YES));
  }

  @Override public void showDowngradingMessage() {
    Snackbar.make(getView(), R.string.downgrading_msg, Snackbar.LENGTH_SHORT)
        .show();
  }

  @Override public Observable<Void> reactionsButtonClicked() {
    return RxView.clicks(reactButton);
  }

  @Override public Observable<Void> reactionsButtonLongPressed() {
    return RxView.longClicks(reactButton);
  }

  @Override public void showTopReactions(String userReaction, List<TopReaction> reactions,
      int numberOfReactions) {
    setUserReaction(userReaction);
    topReactionsPreview.setReactions(reactions, numberOfReactions, getContext());
  }

  @Override public void showReactionsPopup(String cardId, String groupId) {
    ReactionsPopup reactionsPopup = new ReactionsPopup(getContext(), reactButton);
    reactionsPopup.show();
    reactionsPopup.setOnReactionsItemClickListener(item -> {
      reactionEventListener.onNext(new ReactionEvent(cardId, mapUserReaction(item), groupId));
      reactionsPopup.dismiss();
      reactionsPopup.setOnReactionsItemClickListener(null);
    });
  }

  @Override public Observable<ReactionEvent> reactionClicked() {
    return reactionEventListener;
  }

  @Override public void setUserReaction(String reaction) {
    if (topReactionsPreview.isReactionValid(reaction)) {
      reactButton.setImageResource(mapReaction(reaction));
    } else {
      reactButton.setImageResource(R.drawable.ic_reaction_emoticon);
    }
  }

  @Override public void showLoginDialog() {
    Snackbar.make(getView(), getString(R.string.editorial_reactions_login_short),
        Snackbar.LENGTH_LONG)
        .setAction(R.string.login, snackView -> snackListener.onNext(null))
        .show();
  }

  @Override public Observable<Void> snackLoginClick() {
    return snackListener;
  }

  @Override public void showGenericErrorToast() {
    Snackbar.make(getView(), getString(R.string.error_occured), Snackbar.LENGTH_LONG)
        .show();
  }

  @Override public void showNetworkErrorToast() {
    Snackbar.make(getView(), getString(R.string.connection_error), Snackbar.LENGTH_LONG)
        .show();
  }

  private void populateAppContent(EditorialViewModel editorialViewModel) {
    placeHolderPositions = editorialViewModel.getPlaceHolderPositions();
    shouldAnimate = editorialViewModel.shouldHaveAnimation();
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
    setBottomAppCardInfo(editorialViewModel);
  }

  private void setBottomAppCardInfo(EditorialViewModel editorialViewModel) {
    if (editorialViewModel.shouldHaveAnimation()) {
      appCardTitle.setText(editorialViewModel.getBottomCardAppName());
      appCardTitle.setVisibility(View.VISIBLE);
      ImageLoader.with(getContext())
          .load(editorialViewModel.getBottomCardIcon(), appCardImage);
      appCardView.setVisibility(View.VISIBLE);
    }
  }

  private void populateCardContent(EditorialViewModel editorialViewModel) {
    if (editorialViewModel.hasContent()) {
      editorialItemsCard.setVisibility(View.VISIBLE);
      adapter.add(editorialViewModel.getContentList(), editorialViewModel.shouldHaveAnimation());
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

  private boolean isScrollDown(int oldY, int newY) {
    return newY > oldY;
  }

  private boolean isItemShown() {
    if (placeHolderPositions != null && !placeHolderPositions.isEmpty()) {
      EditorialItemsViewHolder placeHolderViewHolder =
          getViewHolderForAdapterPosition(placeHolderPositions.get(0));
      return placeHolderViewHolder != null && placeHolderViewHolder.isVisible(screenHeight,
          screenWidth);
    }
    return false;
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
            if (!isRemoveBottomCard) {
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

  private EditorialItemsViewHolder getViewHolderForAdapterPosition(int placeHolderPosition) {
    if (placeHolderPosition != -1) {
      EditorialItemsViewHolder placeHolderViewHolder =
          ((EditorialItemsViewHolder) editorialItems.findViewHolderForAdapterPosition(
              placeHolderPosition));
      if (placeHolderViewHolder == null) {
        Log.e(TAG, "Unable to find editorialViewHolder");
      }
      return placeHolderViewHolder;
    }
    return null;
  }
}
