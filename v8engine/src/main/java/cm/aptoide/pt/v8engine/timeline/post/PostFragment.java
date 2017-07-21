package cm.aptoide.pt.v8engine.timeline.post;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.install.InstalledRepository;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.view.account.AccountNavigator;
import cm.aptoide.pt.v8engine.view.custom.SimpleDividerItemDecoration;
import cm.aptoide.pt.v8engine.view.fragment.FragmentView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.List;
import rx.Completable;
import rx.Observable;

public class PostFragment extends FragmentView implements PostView {

  private static final int MAX_CHARACTERS = 200;
  private static final String DATA_TO_SHARE = "data_to_share";
  private ProgressBar previewLoading;
  private RecyclerView relatedApps;
  private AppCompatEditText userInput;
  private ImageView previewImage;
  private TextView previewTitle;
  private InstalledRepository installedRepository;
  private Toolbar toolbar;
  private PublishRelay<Void> cancelClick;
  private PublishRelay<Void> postClick;
  private RelatedAppsAdapter adapter;
  private ScrollView scrollView;
  private View previewLayout;
  private PublishRelay<Void> loginAction;

  public static PostFragment newInstance(String toShare) {
    Bundle args = new Bundle();
    args.putString(DATA_TO_SHARE, toShare);

    PostFragment fragment = new PostFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    installedRepository = RepositoryFactory.getInstalledRepository();
    cancelClick = PublishRelay.create();
    postClick = PublishRelay.create();
    loginAction = PublishRelay.create();
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

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupViews();
  }

  @Override public void onDestroyView() {
    destroyLoading(previewLoading);
    previewLoading = null;
    userInput = null;
    previewImage = null;
    previewTitle = null;
    previewLoading = null;
    relatedApps = null;
    toolbar = null;
    scrollView = null;
    previewLayout = null;
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

  private void bindViews(@Nullable View view) {
    userInput = (AppCompatEditText) view.findViewById(R.id.input_text);
    previewImage = (ImageView) view.findViewById(R.id.preview_image);
    previewTitle = (TextView) view.findViewById(R.id.preview_title);
    previewLoading = (ProgressBar) view.findViewById(R.id.preview_progress_bar);
    relatedApps = (RecyclerView) view.findViewById(R.id.related_apps_list);
    toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
    previewLayout = view.findViewById(R.id.preview_layout);
  }

  private void destroyLoading(ProgressBar progressBar) {
    if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
      progressBar.setVisibility(View.GONE);
    }
  }

  private void setupViews() {
    Bundle args = getArguments();
    userInput.setFilters(new InputFilter[] { new InputFilter.LengthFilter(MAX_CHARACTERS) });
    String toShare = null;
    if (args != null && args.containsKey(DATA_TO_SHARE)) {
      toShare = args.getString(DATA_TO_SHARE);
      if (toShare.length() > MAX_CHARACTERS) {
        toShare = toShare.substring(0, MAX_CHARACTERS);
      }
    }

    relatedApps.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    adapter = new RelatedAppsAdapter();
    relatedApps.addItemDecoration(new SimpleDividerItemDecoration(getContext(), 10));
    relatedApps.setAdapter(adapter);
    relatedApps.setHorizontalScrollBarEnabled(false);

    V8Engine v8Engine = (V8Engine) getContext().getApplicationContext();

    final PostRemoteAccessor postRemoteAccessor =
        new PostRemoteAccessor(v8Engine.getDefaultSharedPreferences(),
            v8Engine.getBaseBodyInterceptorV7(), v8Engine.getDefaultClient(),
            WebService.getDefaultConverter(), v8Engine.getTokenInvalidator());

    setUpToolbar();

    final PostLocalAccessor postLocalAccessor = new PostLocalAccessor(installedRepository);
    AptoideAccountManager accountManager = v8Engine.getAccountManager();
    final PostPresenter presenter = new PostPresenter(this, CrashReport.getInstance(),
        new PostManager(postRemoteAccessor, postLocalAccessor, accountManager),
        getFragmentNavigator(), new UrlValidator(Patterns.WEB_URL), toShare,
        new AccountNavigator(getFragmentNavigator(), accountManager, getActivityNavigator()));
    attachPresenter(presenter, null);
  }

  private void setUpToolbar() {
    if (toolbar != null && getActivity() instanceof AppCompatActivity) {
      ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
      setHasOptionsMenu(true);
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
      previewImage.setVisibility(View.VISIBLE);
      ImageLoader.with(getContext())
          .loadWithoutResizeCenterCrop(suggestion.getImage(), previewImage);
    }
    previewTitle.setText(suggestion.getTitle());
    previewTitle.setVisibility(View.VISIBLE);
    previewLayout.setVisibility(View.VISIBLE);
  }

  @Override public void showCardPreviewLoading() {
    previewLayout.setVisibility(View.VISIBLE);
    previewLoading.setVisibility(View.VISIBLE);
  }

  @Override public void hideCardPreviewLoading() {
    previewLoading.setVisibility(View.GONE);
    hidePreviewLayout();
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
    previewTitle.setVisibility(View.INVISIBLE);
    hidePreviewLayout();
  }

  @Override public void showGenericError() {
    ShowMessage.asSnack(this, R.string.all_message_general_error);
  }

  @Override public void showInvalidTextError() {
    ShowMessage.asSnack(this, R.string.timeline_message_write_something);
  }

  @Override public void showInvalidPackageError() {
    ShowMessage.asSnack(this, R.string.timeline_message_pick_an_app);
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
    hideCardPreview();
  }

  @Override public void exit() {
    getActivity().onBackPressed();
  }

  @Override public void showNoLoginError() {
    Snackbar.make(getView(), R.string.you_need_to_be_logged_in, BaseTransientBottomBar.LENGTH_LONG)
        .setAction(R.string.login, view -> loginAction.call(null))
        .show();
  }

  @Override public Observable<Void> getLoginClick() {
    return loginAction;
  }

  private void hidePreviewLayout() {
    if (previewImage.getVisibility() == View.GONE
        && previewLoading.getVisibility() == View.GONE
        && previewTitle.getVisibility() == View.INVISIBLE) {
      previewLayout.setVisibility(View.GONE);
    }
  }

  private String getInputText() {
    return userInput.getText()
        .toString();
  }
}
