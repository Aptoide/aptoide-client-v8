package cm.aptoide.pt.view.updates.rollback;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.ads.MinimalAdMapper;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.accessors.RollbackAccessor;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.install.InstallFabricEvents;
import cm.aptoide.pt.install.Installer;
import cm.aptoide.pt.install.InstallerFactory;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.fragment.AptoideBaseFragment;
import cm.aptoide.pt.view.recycler.BaseAdapter;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import cm.aptoide.pt.view.recycler.displayable.FooterRowDisplayable;
import com.crashlytics.android.answers.Answers;
import com.facebook.appevents.AppEventsLogger;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RollbackFragment extends AptoideBaseFragment<BaseAdapter> {

  private static final SimpleDateFormat dateFormat =
      new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
  private TextView emptyData;
  private Installer installManager;
  private Analytics analytics;
  private String marketName;

  public RollbackFragment() {
  }

  public static Fragment newInstance() {
    return new RollbackFragment();
  }

  @Override protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override protected void setupToolbarDetails(Toolbar toolbar) {
    toolbar.setTitle(R.string.rollback);
  }

  @Override public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_clear, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();

    if (itemId == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    } else if (itemId == R.id.menu_clear) {
      //DeprecatedDatabase.RollbackQ.deleteAll(realm);
      AccessorFactory.getAccessorFor(((AptoideApplication) getContext().getApplicationContext()
          .getApplicationContext()).getDatabase(), Rollback.class)
          .removeAll();
      clearDisplayables();
      finishLoading();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    analytics = Analytics.getInstance();
    marketName = ((AptoideApplication) getContext().getApplicationContext()
        .getApplicationContext()).getMarketName();
    installManager = new InstallerFactory(new MinimalAdMapper(),
        new InstallFabricEvents(Analytics.getInstance(), Answers.getInstance(),
            AppEventsLogger.newLogger(getContext().getApplicationContext())),
        ((AptoideApplication) getContext().getApplicationContext()
            .getApplicationContext()).getImageCachePath()).create(getContext(),
        InstallerFactory.ROLLBACK);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_with_toolbar;
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    emptyData = (TextView) view.findViewById(R.id.empty_data);
    setHasOptionsMenu(true);
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);
    AptoideUtils.ThreadU.runOnUiThread(() -> fetchRollbacks());
  }

  @UiThread private void fetchRollbacks() {
    RollbackAccessor rollbackAccessor = AccessorFactory.getAccessorFor(
        ((AptoideApplication) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Rollback.class);
    rollbackAccessor.getConfirmedRollbacks()
        .observeOn(Schedulers.computation())
        .map(rollbacks -> createDisplayables(rollbacks))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(rollbacks -> {
          if (rollbacks == null || rollbacks.isEmpty()) {
            emptyData.setText(AptoideUtils.StringU.getFormattedString(R.string.no_rollbacks_msg,
                getContext().getResources(), marketName));
            emptyData.setVisibility(View.VISIBLE);
          } else {
            emptyData.setVisibility(View.GONE);
            clearDisplayables().addDisplayables(rollbacks, true);
          }
          finishLoading();
        });
  }

  private List<Displayable> createDisplayables(List<Rollback> rollbacks) {
    List<Displayable> displayables = new LinkedList<>();
    long lastDay = 0;
    for (int i = 0; i < rollbacks.size(); i++) {
      Rollback rollback = rollbacks.get(i);
      long daysAgo = TimeUnit.MILLISECONDS.toDays(rollback.getTimestamp());
      if (lastDay != daysAgo) {
        lastDay = daysAgo;
        displayables.add(new FooterRowDisplayable(dateFormat.format(rollback.getTimestamp())));
      }
      displayables.add(new RollbackDisplayable(installManager, rollback, marketName));
    }

    Calendar.getInstance(Locale.getDefault());
    return displayables;
  }
}
