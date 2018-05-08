package cm.aptoide.pt.app.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.app.view.screenshots.NewScreenshotsAdapter;
import cm.aptoide.pt.app.view.screenshots.ScreenShotClickEvent;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.app.DetailedApp;
import cm.aptoide.pt.view.fragment.BaseToolbarFragment;
import java.util.ArrayList;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by franciscocalado on 07/05/18.
 */

public class NewAppViewFragment extends BaseToolbarFragment implements AppViewView {
  private static final String ORIGIN_TAG = "TAG";

  @Inject AppViewPresenter presenter;
  private Menu menu;
  private long appId;
  private String packageName;
  private NewScreenshotsAdapter screenshotsAdapter;
  private PublishSubject<ScreenShotClickEvent> screenShotClick;
  private PublishSubject<ReadMoreClickEvent> readMoreClick;

  //Views
  private ImageView appIcon;
  private ImageView trustedBadge;
  private TextView appName;
  private TextView trustedText;
  private TextView downloadsTop;
  private TextView sizeInfo;
  private Button installButton;
  private TextView appcValue;
  private TextView latestVersion;
  private RecyclerView screenshots;
  private TextView descriptionText;
  private Button descriptionReadMore;
  private TextView reviewUsers;
  private TextView avgReviewScore;
  private RecyclerView commentsView;
  private Button rateAppButton;
  private Button showAllCommentsButton;
  /*
  TODO:Flag this app stuff missing here!
   */
  private ImageView storeIcon;
  private TextView storeName;
  private TextView storeFollowers;
  private TextView storeDownloads;
  private RecyclerView similarApps;
  private TextView infoWebsite;
  private TextView infoEmail;
  private TextView infoPrivacy;

  private ProgressBar viewProgress;
  private View appview;

  public static NewAppViewFragment newInstance(long appId, String packageName,
      AppViewFragment.OpenType openType, String tag) {
    Bundle bundle = new Bundle();
    bundle.putString(ORIGIN_TAG, tag);
    bundle.putLong(AppViewFragment.BundleKeys.APP_ID.name(), appId);
    bundle.putString(AppViewFragment.BundleKeys.PACKAGE_NAME.name(), packageName);
    bundle.putSerializable(AppViewFragment.BundleKeys.SHOULD_INSTALL.name(), openType);
    NewAppViewFragment fragment = new NewAppViewFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    screenShotClick = PublishSubject.create();
    readMoreClick = PublishSubject.create();
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);

    appId = args.getLong(AppViewFragment.BundleKeys.APP_ID.name(), -1);
    packageName = args.getString(AppViewFragment.BundleKeys.PACKAGE_NAME.name(), null);
  }

  @Override public void setupViews() {
    super.setupViews();

    screenshotsAdapter =
        new NewScreenshotsAdapter(new ArrayList<>(), new ArrayList<>(), screenShotClick);
    screenshots.setAdapter(screenshotsAdapter);

    attachPresenter(presenter);
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);

    appIcon = (ImageView) view.findViewById(R.id.app_icon);
    trustedBadge = (ImageView) view.findViewById(R.id.trusted_badge);
    appName = (TextView) view.findViewById(R.id.app_name);
    trustedText = (TextView) view.findViewById(R.id.trusted_text);
    downloadsTop = (TextView) view.findViewById(R.id.header_downloads);
    sizeInfo = (TextView) view.findViewById(R.id.header_size);
    installButton = (Button) view.findViewById(R.id.install_button);
    appcValue = (TextView) view.findViewById(R.id.appc_layout)
        .findViewById(R.id.appcoins_reward_message);
    latestVersion = (TextView) view.findViewById(R.id.versions_layout)
        .findViewById(R.id.latest_version);

    screenshots = (RecyclerView) view.findViewById(R.id.screenshots_list);
    screenshots.setLayoutManager(
        new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
    screenshots.setNestedScrollingEnabled(false);

    descriptionText = (TextView) view.findViewById(R.id.description_text);
    descriptionReadMore = (Button) view.findViewById(R.id.description_see_more);
    reviewUsers = (TextView) view.findViewById(R.id.users_voted);
    avgReviewScore = (TextView) view.findViewById(R.id.rating_value);
    commentsView = (RecyclerView) view.findViewById(R.id.top_comments_list);
    rateAppButton = (Button) view.findViewById(R.id.rate_this_button);
    showAllCommentsButton = (Button) view.findViewById(R.id.read_all_button);
    storeIcon = (ImageView) view.findViewById(R.id.store_icon);
    storeName = (TextView) view.findViewById(R.id.store_name);
    storeFollowers = (TextView) view.findViewById(R.id.user_count);
    storeDownloads = (TextView) view.findViewById(R.id.download_count);
    similarApps = (RecyclerView) view.findViewById(R.id.similar_list);
    infoWebsite = (TextView) view.findViewById(R.id.website_label);
    infoEmail = (TextView) view.findViewById(R.id.email_label);
    infoPrivacy = (TextView) view.findViewById(R.id.privacy_policy_label);

    viewProgress = (ProgressBar) view.findViewById(R.id.appview_progress);
    appview = view.findViewById(R.id.appview_full);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build("AppViewFragment", "", StoreContext.meta);
  }

  @Override public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    this.menu = menu;
    inflater.inflate(R.menu.fragment_appview, menu);
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_new_app_view;
  }

  @Override public void showLoading() {
    appview.setVisibility(View.GONE);
    viewProgress.setVisibility(View.VISIBLE);
  }

  @Override public void showAppview() {
    appview.setVisibility(View.VISIBLE);
    viewProgress.setVisibility(View.GONE);
  }

  @Override public long getAppId() {
    return appId;
  }

  @Override public String getPackageName() {
    return packageName;
  }

  @Override public void populateAppDetails(DetailedApp detailedApp) {
    appName.setText(detailedApp.getName());
    ImageLoader.with(getContext())
        .load(detailedApp.getIcon(), appIcon);
    downloadsTop.setText(String.format("%s", AptoideUtils.StringU.withSuffix(detailedApp.getStats()
        .getPdownloads())));
    sizeInfo.setText(AptoideUtils.StringU.formatBytes(detailedApp.getSize(), false));
    storeName.setText(detailedApp.getStore()
        .getName());
    ImageLoader.with(getContext())
        .loadWithShadowCircleTransform(detailedApp.getStore()
            .getAvatar(), storeIcon);
    storeDownloads.setText(String.format("%s", AptoideUtils.StringU.withSuffix(
        detailedApp.getStore()
            .getStats()
            .getDownloads())));
    storeFollowers.setText(String.format("%s", AptoideUtils.StringU.withSuffix(
        detailedApp.getStore()
            .getStats()
            .getSubscribers())));
    screenshotsAdapter.updateScreenshots(detailedApp.getMedia()
        .getScreenshots());
    screenshotsAdapter.updateVideos(detailedApp.getMedia()
        .getVideos());
    setDescription(detailedApp.getMedia()
        .getDescription());
    setReadMoreClickListener(detailedApp);
    showAppview();
  }

  @Override public Observable<ScreenShotClickEvent> getScreenshotClickEvent() {
    return screenShotClick;
  }

  @Override public Observable<ReadMoreClickEvent> clickedReadMore() {
    return readMoreClick;
  }

  private void setDescription(String description) {
    if (!TextUtils.isEmpty(description)) {
      descriptionText.setText(AptoideUtils.HtmlU.parse(description));
    } else {
      // only show "default" description if the app doesn't have one
      descriptionText.setText(R.string.description_not_available);
      descriptionReadMore.setVisibility(View.GONE);
    }
  }

  private void setReadMoreClickListener(DetailedApp detailedApp) {
    descriptionReadMore.setOnClickListener(view -> readMoreClick.onNext(
        new ReadMoreClickEvent(detailedApp.getName(), detailedApp.getMedia()
            .getDescription(), detailedApp.getStore()
            .getAppearance()
            .getTheme())));
  }
}
