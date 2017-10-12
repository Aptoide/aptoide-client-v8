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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.GetLeaderboardEntriesResponse;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.social.leaderboard.data.Leaderboard;
import cm.aptoide.pt.social.leaderboard.data.LeaderboardEntry;
import cm.aptoide.pt.social.leaderboard.presenter.LeaderboardNavigator;
import cm.aptoide.pt.social.leaderboard.presenter.LeaderboardOnItemSelectedListener;
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
  private TextView userScore;
  private TextView userName;
  private TokenInvalidator tokenInvalidator;
  private SharedPreferences sharedPreferences;
  private Toolbar toolbar;
  private BackButton backButton;
  private TabNavigator tabNavigator;
  private PublishSubject<LeaderboardEntry> leaderboardEntryPublishSubject;
  private PublishSubject<String> spinnerPublishSubject;
  private LeaderboardPresenter presenter;
  private Spinner leaderboardSpinner;

  private ImageView firstImage;
  private TextView firstName;
  private TextView firstScore;

  private ImageView secondImage;
  private TextView secondName;
  private TextView secondScore;

  private ImageView thirdImage;
  private TextView thirdName;
  private TextView thirdScore;


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
    this.spinnerPublishSubject = PublishSubject.create();
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
    userName.setText(entries.get(0).get(0).getName());
    userScore.setText(String.valueOf(entries.get(0).get(0).getScore()));

    firstName.setText(entries.get(1).get(0).getName());
    firstScore.setText(String.valueOf(entries.get(1).get(0).getScore()));

    secondName.setText(entries.get(1).get(1).getName());
    secondScore.setText(String.valueOf(entries.get(1).get(1).getScore()));

    thirdName.setText(entries.get(1).get(2).getName());
    thirdScore.setText(String.valueOf(entries.get(1).get(2).getScore()));

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
    final Leaderboard leaderboard =  new Leaderboard(baseBodyInterceptorV7, defaultClient, defaultConverter, tokenInvalidator, sharedPreferences);

    userIcon = (ImageView) view.findViewById(R.id.current_user_icon);
    userName = (TextView) view.findViewById(R.id.current_user_name);
    userScore = (TextView) view.findViewById(R.id.current_user_score);

    firstImage = (ImageView) view.findViewById(R.id.user_1);
    firstName = (TextView) view.findViewById(R.id.user_1_name);
    firstScore = (TextView) view.findViewById(R.id.user_1_score);

    secondImage = (ImageView) view.findViewById(R.id.user_2);
    secondName = (TextView) view.findViewById(R.id.user_2_name);
    secondScore = (TextView) view.findViewById(R.id.user_2_score);

    thirdImage = (ImageView) view.findViewById(R.id.user_3);
    thirdName = (TextView) view.findViewById(R.id.user_3_name);
    thirdScore = (TextView) view.findViewById(R.id.user_3_score);


    list = (RecyclerView) view.findViewById(R.id.fragment_leaderboard_entries);
    list.setLayoutManager(new LinearLayoutManager(getContext()));
    list.setAdapter(adapter);

    leaderboardSpinner = (Spinner) view.findViewById(R.id.leaderboard_spinner);
    ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.leaderboard_type, android.R.layout.simple_spinner_item);
    spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
    leaderboardSpinner.setAdapter(spinnerAdapter);
    leaderboardSpinner.setOnItemSelectedListener(new LeaderboardOnItemSelectedListener(adapter, spinnerPublishSubject));

    LeaderboardNavigator leaderboardNavigator =
        new LeaderboardNavigator(getFragmentNavigator(), tabNavigator);
    attachPresenter(new LeaderboardPresenter(this, leaderboard, CrashReport.getInstance(), leaderboardNavigator,
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

  public Observable<String> spinnerChoice(){
    return spinnerPublishSubject;
  }
}
