package cm.aptoide.pt.timeline.post;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.navigator.TabNavigator;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.view.BackButtonActivity;
import cm.aptoide.pt.view.custom.SimpleDividerItemDecoration;
import cm.aptoide.pt.view.fragment.FragmentView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.List;
import javax.inject.Inject;
import rx.Completable;
import rx.Observable;

public class PostFragment extends FragmentView implements PostView {
  public static final String OPEN_SOURCE = "open_source";
  private static final int MAX_CHARACTERS = 200;
  @Inject AnalyticsManager analyticsManager;
  private ProgressBar previewLoading;
  private RecyclerView relatedApps;
  private EditText userInput;
  private ImageView previewImage;
  private TextView previewTitle;
  private TextView urlShower;
  private InstalledRepository installedRepository;
  private Toolbar toolbar;
  private PublishRelay<Void> cancelClick;
  private PublishRelay<Void> postClick;
  private RelatedAppsAdapter adapter;
  private ScrollView scrollView;
  private View previewLayout;
  private PublishRelay<Void> loginAction;
  private PublishRelay<Void> openUploaderButton;
  private PostPresenter presenter;
  private View inputSeparator;
  private PostAnalytics analytics;
  private TabNavigator tabNavigator;
  private PostUrlProvider externalUrlProvider;

  public static Fragment newInstanceFromExternalSource() {
    return newInstance(PostAnalytics.OpenSource.EXTERNAL);
  }

  @NonNull private static Fragment newInstance(PostAnalytics.OpenSource openSource) {
    PostFragment postFragment = new PostFragment();
    Bundle args = new Bundle();
    args.putSerializable(OPEN_SOURCE, openSource);
    postFragment.setArguments(args);
    return postFragment;
  }

  public static Fragment newInstanceFromTimeline() {
    return newInstance(PostAnalytics.OpenSource.APP_TIMELINE);
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (activity instanceof TabNavigator) {
      tabNavigator = (TabNavigator) activity;
    } else {
      throw new IllegalStateException(
          "Activity must implement " + TabNavigator.class.getSimpleName());
    }

    if (activity instanceof PostUrlProvider) {
      externalUrlProvider = (PostUrlProvider) activity;
    } else {
      externalUrlProvider = null;
    }
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    final AptoideApplication application =
        (AptoideApplication) getContext().getApplicationContext();
    installedRepository = RepositoryFactory.getInstalledRepository(getContext());
    cancelClick = PublishRelay.create();
    postClick = PublishRelay.create();
    loginAction = PublishRelay.create();
    openUploaderButton = PublishRelay.create();
    analytics = new PostAnalytics(application.getNavigationTracker(), analyticsManager);
    handleAnalytics();
    setHasOptionsMenu(true);
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_timeline_post, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      cancelClick.call(null);
      return true;
    } else if (item.getItemId() == R.id.post_button) {
      postClick.call(null);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void handleAnalytics() {
    analytics.sendOpenEvent((PostAnalytics.OpenSource) getArguments().getSerializable(OPEN_SOURCE),
        isExternalOpen());
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupViews();
  }

  @Override public void onDestroyView() {
    destroyLoading(previewLoading);
    hideKeyboard();
    previewLoading = null;
    userInput = null;
    previewImage = null;
    previewTitle = null;
    previewLoading = null;
    relatedApps = null;
    toolbar = null;
    scrollView = null;
    previewLayout = null;
    inputSeparator = null;
    urlShower = null;
    ((BackButtonActivity) getActivity()).unregisterClickHandler(presenter);
    super.onDestroyView();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(getContentViewId(), container, false);
    bindViews(root);
    return root;
  }

  @LayoutRes private int getContentViewId() {
    return R.layout.fragment_post;
  }

  private void bindViews(View view) {
    userInput = (EditText) view.findViewById(R.id.input_text);
    previewImage = (ImageView) view.findViewById(R.id.preview_image);
    previewTitle = (TextView) view.findViewById(R.id.preview_title);
    previewLoading = (ProgressBar) view.findViewById(R.id.preview_progress_bar);
    relatedApps = (RecyclerView) view.findViewById(R.id.related_apps_list);
    toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
    previewLayout = view.findViewById(R.id.preview_layout);
    inputSeparator = view.findViewById(R.id.input_text_separator);
    urlShower = (TextView) view.findViewById(R.id.url_shower);
  }

  private void destroyLoading(ProgressBar progressBar) {
    if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
      progressBar.setVisibility(View.GONE);
    }
  }

  private void setupViews() {
    userInput.setFilters(new InputFilter[] { new InputFilter.LengthFilter(MAX_CHARACTERS) });

    relatedApps.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    adapter = new RelatedAppsAdapter();
    relatedApps.addItemDecoration(new SimpleDividerItemDecoration(getContext(), 5));
    relatedApps.setAdapter(adapter);
    relatedApps.setHorizontalScrollBarEnabled(false);
    userInput.requestFocus();

    AptoideApplication aptoideApplication =
        (AptoideApplication) getContext().getApplicationContext();

    final PostRemoteAccessor postRemoteAccessor =
        new PostRemoteAccessor(aptoideApplication.getDefaultSharedPreferences(),
            aptoideApplication.getAccountSettingsBodyInterceptorPoolV7(),
            aptoideApplication.getDefaultClient(), WebService.getDefaultConverter(),
            aptoideApplication.getTokenInvalidator());

    setUpToolbar();
    showKeyboard();
    final PostLocalAccessor postLocalAccessor = new PostLocalAccessor(installedRepository);
    AptoideAccountManager accountManager = aptoideApplication.getAccountManager();
    PostUrlProvider urlProvider;
    if (getActivity() instanceof PostUrlProvider) {
      urlProvider = (PostUrlProvider) getActivity();
    } else {
      urlProvider = () -> null;
    }
    if (urlProvider.getUrlToShare() != null && !urlProvider.getUrlToShare()
        .isEmpty()) {
      userInput.setHint(R.string.timeline_message_share_on_timeline_external);
    }
    presenter = new PostPresenter(this, CrashReport.getInstance(),
        new PostManager(postRemoteAccessor, postLocalAccessor, accountManager),
        getFragmentNavigator(), new UrlValidator(Patterns.WEB_URL),
        ((ActivityResultNavigator) getContext()).getAccountNavigator(), tabNavigator, analytics);
    ((BackButtonActivity) getActivity()).registerClickHandler(presenter);
    attachPresenter(presenter);
  }

  private void showKeyboard() {
    InputMethodManager imm =
        (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.showSoftInput(userInput, InputMethodManager.SHOW_IMPLICIT);
  }

  private void setUpToolbar() {
    if (toolbar != null && getActivity() instanceof AppCompatActivity) {
      ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
      toolbar.setEnabled(true);
      ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeButtonEnabled(true);
      actionBar.setTitle(R.string.timeline_title_new_post);
    }
  }

  @Override public Observable<String> onInputTextChanged() {
    return RxTextView.textChanges(userInput)
        .map(data -> data.toString());
  }

  @Override public Observable<String> shareButtonPressed() {
    return postClick.map(__ -> getInputText());
  }

  @Override public Observable<Void> cancelButtonPressed() {
    return cancelClick;
  }

  @Override public Completable showSuccessMessage() {
    return ShowMessage.asLongObservableSnack(getActivity(), R.string.title_successful);
  }

  @Override public void showCardPreview(PostPreview suggestion) {
    if (suggestion.getImage() != null) {
      Resources r = getResources();
      int radius =
          (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, r.getDisplayMetrics());
      int margin =
          (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics());
      previewImage.setVisibility(View.VISIBLE);
      ImageLoader.with(getContext())
          .loadWithRoundCorners(suggestion.getImage(), radius, margin, previewImage);
    }

    if (suggestion.getTitle() == null || suggestion.getTitle()
        .isEmpty()) {
      previewTitle.setText(suggestion.getUrl());
      urlShower.setVisibility(View.GONE);
    } else {
      urlShower.setText(suggestion.getUrl());
      urlShower.setVisibility(View.VISIBLE);
      previewTitle.setText(suggestion.getTitle());
    }
    previewTitle.setVisibility(View.VISIBLE);
    handlePreviewLayout();
  }

  @Override public void showCardPreviewLoading() {
    previewLoading.setVisibility(View.VISIBLE);
    handlePreviewLayout();
  }

  @Override public void hideCardPreviewLoading() {
    previewLoading.setVisibility(View.GONE);
    handlePreviewLayout();
  }

  @Override public void showRelatedAppsLoading() {
    adapter.showLoading();
    relatedApps.scrollToPosition(0);
  }

  @Override public void hideRelatedAppsLoading() {
    adapter.hideLoading();
  }

  @Override public void hideCardPreview() {
    previewImage.setVisibility(View.GONE);
    previewTitle.setVisibility(View.GONE);
    urlShower.setVisibility(View.GONE);
    handlePreviewLayout();
  }

  @Override public void showGenericError() {
    Snackbar.make(toolbar, R.string.all_message_general_error, Snackbar.LENGTH_LONG)
        .show();
  }

  @Override public void showInvalidTextError() {
    Snackbar.make(toolbar, R.string.timeline_message_write_something, Snackbar.LENGTH_LONG)
        .show();
  }

  @Override public void showInvalidPackageError() {
    Snackbar.make(toolbar, R.string.timeline_message_pick_an_app, Snackbar.LENGTH_LONG)
        .show();
    scrollView.smoothScrollTo(scrollView.getLeft(), scrollView.getBottom());
  }

  @Override public void addRelatedApps(List<PostRemoteAccessor.RelatedApp> relatedAppsList) {
    adapter.addRelatedApps(relatedAppsList);
    relatedApps.scrollToPosition(0);
  }

  @Override public PostRemoteAccessor.RelatedApp getCurrentSelected() {
    return adapter.getCurrentSelected();
  }

  @Override public void clearRemoteRelated() {
    adapter.clearRemoteRelated();
  }

  @Override public Observable<PostRemoteAccessor.RelatedApp> getClickedView() {
    return adapter.getClickedView();
  }

  @Override public Completable setRelatedAppSelected(PostRemoteAccessor.RelatedApp app) {
    return adapter.setRelatedAppSelected(app);
  }

  @Override public void hideCardPreviewTitle() {
    previewTitle.setVisibility(View.GONE);
    urlShower.setVisibility(View.GONE);
    hideCardPreview();
  }

  @Override public void exit() {
    getActivity().finish();
  }

  @Override public void showNoLoginError() {
    Snackbar.make(toolbar, R.string.you_need_to_be_logged_in, Snackbar.LENGTH_LONG)
        .setAction(R.string.login, view -> loginAction.call(null))
        .show();
  }

  @Override public Observable<Void> getLoginClick() {
    return loginAction;
  }

  @Override public void showAppNotFoundError() {
    Snackbar.make(toolbar, R.string.timeline_message_upload_app, Snackbar.LENGTH_LONG)
        .setAction(R.string.timeline_button_open_uploader, view -> openUploaderButton.call(null))
        .show();
  }

  @Override public Observable<Void> getAppNotFoundErrorAction() {
    return openUploaderButton;
  }

  @Override public void clearAllRelated() {
    adapter.clearAllRelated();
  }

  @Override public int getPreviewVisibility() {
    return previewImage.getVisibility();
  }

  @Override public void showInvalidUrlError() {
    Snackbar.make(toolbar, R.string.ws_error_IARG_105, Snackbar.LENGTH_LONG)
        .show();
  }

  @Override public String getExternalUrlToShare() {
    return externalUrlProvider != null ? externalUrlProvider.getUrlToShare() : null;
  }

  @Override public boolean isExternalOpen() {
    return getExternalUrlToShare() != null && !getExternalUrlToShare().trim()
        .isEmpty();
  }

  private void handlePreviewLayout() {
    if (previewImage.getVisibility() == View.GONE
        && previewLoading.getVisibility() == View.GONE
        && previewTitle.getVisibility() == View.GONE) {
      previewLayout.setVisibility(View.GONE);
      inputSeparator.setVisibility(View.VISIBLE);
    } else {
      previewLayout.setVisibility(View.VISIBLE);
      inputSeparator.setVisibility(View.GONE);
    }
  }

  private String getInputText() {
    return userInput.getText()
        .toString();
  }
}
