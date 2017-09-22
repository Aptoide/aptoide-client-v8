package cm.aptoide.pt.social.leaderboard.view;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.social.leaderboard.data.Leaderboard;
import cm.aptoide.pt.social.leaderboard.data.LeaderboardEntry;
import cm.aptoide.pt.social.leaderboard.presenter.LeaderboardNavigator;
import cm.aptoide.pt.social.leaderboard.presenter.LeaderboardPresenter;
import cm.aptoide.pt.view.BackButton;
import cm.aptoide.pt.view.fragment.FragmentView;
import cm.aptoide.pt.view.navigator.TabNavigator;
import com.trello.rxlifecycle.LifecycleTransformer;
import java.util.Collections;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by franciscocalado on 9/22/17.
 */

public class LeaderboardFragment extends FragmentView implements LeaderboardView {

  private LeaderboardAdapter adapter;
  private RecyclerView list;
  private ImageView userIcon;
  private TextView userRank;
  private TextView userScore;
  private TextView userName;
  private TokenInvalidator tokenInvalidator;
  private SharedPreferences sharedPreferences;
  private Toolbar toolbar;
  private BackButton backButton;
  private TabNavigator tabNavigator;
  private PublishSubject<LeaderboardEntry> leaderboardEntryPublishSubject;
  private LeaderboardPresenter presenter;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_leaderboard, container, false);
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (activity instanceof TabNavigator) {
      tabNavigator = (TabNavigator) activity;
    } else {
      throw new IllegalStateException(
          "Activity must implement " + TabNavigator.class.getSimpleName());
    }
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.adapter =
        new LeaderboardAdapter(Collections.emptyList(), null, leaderboardEntryPublishSubject);
    this.tokenInvalidator = ((AptoideApplication) getContext().getApplicationContext()).getTokenInvalidator();
    this.sharedPreferences =
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences();
    this.leaderboardEntryPublishSubject = PublishSubject.create();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    toolbar = null;
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }

  @Override public void showLeaderboardEntries(List<List<LeaderboardEntry>> entries) {
    adapter.updateLeaderboardEntries(entries);
  }

  @Override public Observable<LeaderboardEntry> postClicked() {
    return leaderboardEntryPublishSubject;
  }

  @Override public void onViewCreated(android.view.View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    bindViews(view);
    setupViews();

    final BodyInterceptor<BaseBody> baseBodyInterceptorV7 =
        ((AptoideApplication) getContext().getApplicationContext()).getBodyInterceptorWebV7();
    final OkHttpClient defaultClient =
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultClient();
    final Converter.Factory defaultConverter = WebService.getDefaultConverter();

    userIcon = (ImageView) view.findViewById(R.id.user_icon);
    userName = (TextView) view.findViewById(R.id.user_name);

    list = (RecyclerView) view.findViewById(R.id.fragment_leaderboard_global);
    list.setLayoutManager(new LinearLayoutManager(getContext()));
    list.setAdapter(adapter);

    LeaderboardNavigator leaderboardNavigator =
        new LeaderboardNavigator(getFragmentNavigator(), tabNavigator);
    attachPresenter(new LeaderboardPresenter(this,
        new Leaderboard(baseBodyInterceptorV7, defaultClient, defaultConverter, tokenInvalidator,
            sharedPreferences), CrashReport.getInstance(), leaderboardNavigator,
        getFragmentNavigator()), savedInstanceState);
  }

  protected boolean hasToolbar() {
    return toolbar != null;
  }

  protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  protected void setupToolbarDetails(Toolbar toolbar) {
    // does nothing. placeholder method.
  }

  public void setupToolbar() {
    if (hasToolbar()) {
      ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
      boolean showUp = displayHomeUpAsEnabled();

      ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
      actionBar.setDisplayHomeAsUpEnabled(showUp);
      actionBar.setTitle("Leaderboard");
      setupToolbarDetails(toolbar);
    }
  }

  public void setupViews() {
    setupToolbar();
  }

  public void bindViews(View view) {

    this.toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    setHasOptionsMenu(true);
  }
}
