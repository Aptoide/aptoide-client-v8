package cm.aptoide.pt.v8engine.timeline.post;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.install.InstalledRepository;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.view.fragment.FragmentView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Completable;
import rx.Observable;

public class PostFragment extends FragmentView implements PostView {

  private static final int MAX_CHARACTERS = 200;
  private static final String DATA_TO_SHARE = "data_to_share";
  private ProgressBar previewLoading;
  private ProgressBar relatedAppsLoading;
  private RecyclerView relatedApps;
  private EditText userInput;
  private ImageView previewImage;
  private TextView previewTitle;
  private TextView relatedAppsHeader;
  private InstalledRepository installedRepository;
  private Toolbar toolbar;
  private PublishRelay<Void> cancelClick;
  private PublishRelay<Void> postClick;

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

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(getContentViewId(), container, false);
    bindViews(root);
    setupViews();
    return root;
  }

  @LayoutRes private int getContentViewId() {
    return R.layout.fragment_post;
  }

  private void bindViews(@Nullable View view) {
    userInput = (EditText) view.findViewById(R.id.input_text);
    previewImage = (ImageView) view.findViewById(R.id.preview_image);
    previewTitle = (TextView) view.findViewById(R.id.preview_title);
    relatedAppsHeader = (TextView) view.findViewById(R.id.related_apps_header);
    previewLoading = (ProgressBar) view.findViewById(R.id.preview_progress_bar);
    relatedAppsLoading = (ProgressBar) view.findViewById(R.id.related_apps_progress_bar);
    relatedApps = (RecyclerView) view.findViewById(R.id.related_apps_list);
    toolbar = (Toolbar) view.findViewById(R.id.toolbar);
  }

  @Override public void onDestroyView() {
    destroyLoading(relatedAppsLoading);
    relatedAppsLoading = null;

    destroyLoading(previewLoading);
    previewLoading = null;

    super.onDestroyView();
  }

  private void destroyLoading(ProgressBar progressBar) {
    if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
      progressBar.setVisibility(View.GONE);
    }
  }

  private void setupViews() {
    Bundle args = getArguments();
    userInput.setFilters(new InputFilter[] { new InputFilter.LengthFilter(MAX_CHARACTERS) });

    if (args != null && args.containsKey(DATA_TO_SHARE)) {
      String toShare = args.getString(DATA_TO_SHARE);
      if (toShare.length() > MAX_CHARACTERS) {
        toShare = toShare.substring(0, MAX_CHARACTERS);
      }
      userInput.setText(toShare);
    }

    relatedApps.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    final RelatedAppsAdapter adapter = new RelatedAppsAdapter();
    relatedApps.setAdapter(adapter);

    V8Engine v8Engine = (V8Engine) getContext().getApplicationContext();

    final PostRemoteAccessor postRemoteAccessor =
        new PostRemoteAccessor(v8Engine.getDefaultSharedPreferences(),
            v8Engine.getBaseBodyInterceptorV7(), v8Engine.getDefaultClient(),
            WebService.getDefaultConverter(), v8Engine.getTokenInvalidator());

    setUpToolbar();

    final PostLocalAccessor postLocalAccessor = new PostLocalAccessor(installedRepository);
    final PostPresenter presenter = new PostPresenter(this, CrashReport.getInstance(),
        new PostManager(postRemoteAccessor, postLocalAccessor), adapter, getFragmentNavigator());
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
      actionBar.setTitle(R.string.timeline_title_post);
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
    previewImage.setVisibility(View.VISIBLE);
    previewTitle.setVisibility(View.VISIBLE);

    ImageLoader.with(getContext())
        .loadWithoutResizing(suggestion.getImage(), previewImage);
    previewTitle.setText(suggestion.getTitle());
  }

  @Override public void showCardPreviewLoading() {
    previewLoading.setVisibility(View.VISIBLE);
  }

  @Override public void hideCardPreviewLoading() {
    previewLoading.setVisibility(View.GONE);
  }

  @Override public void showRelatedAppsLoading() {
    relatedAppsLoading.setVisibility(View.VISIBLE);
    relatedAppsHeader.setVisibility(View.GONE);
  }

  @Override public void hideRelatedAppsLoading() {
    relatedAppsLoading.setVisibility(View.GONE);
    relatedAppsHeader.setVisibility(View.VISIBLE);
  }

  @Override public void hideCardPreview() {
    previewImage.setVisibility(View.GONE);
    previewTitle.setVisibility(View.GONE);
  }

  private String getInputText() {
    return userInput.getText()
        .toString();
  }
}
