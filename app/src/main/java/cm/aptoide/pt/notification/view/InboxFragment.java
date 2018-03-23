package cm.aptoide.pt.notification.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.home.BottomNavigationActivity;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.notification.AptoideNotification;
import cm.aptoide.pt.view.fragment.BaseToolbarFragment;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

/**
 * Created by pedroribeiro on 16/05/17.
 */

public class InboxFragment extends BaseToolbarFragment implements InboxView {

  @Inject AnalyticsManager analyticsManager;
  private RecyclerView list;
  private InboxAdapter adapter;
  private PublishSubject<AptoideNotification> notificationSubject;
  private BottomNavigationActivity bottomNavigationActivity;

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity instanceof BottomNavigationActivity) {
      bottomNavigationActivity = ((BottomNavigationActivity) activity);
    }
  }

  @Override public void onDestroy() {
    bottomNavigationActivity = null;
    super.onDestroy();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();

    if (itemId == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public void onDestroyView() {
    bottomNavigationActivity.show();
    super.onDestroyView();
  }

  @Override protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override protected void setupToolbarDetails(Toolbar toolbar) {
    super.setupToolbarDetails(toolbar);
    toolbar.setTitle(getString(R.string.myaccount_header_title));
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    notificationSubject = PublishSubject.create();
    adapter = new InboxAdapter(Collections.emptyList(), notificationSubject);
    setHasOptionsMenu(true);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    list = (RecyclerView) view.findViewById(R.id.fragment_inbox_list);
    list.setAdapter(adapter);
    list.setLayoutManager(new LinearLayoutManager(getContext()));

    AptoideApplication application = ((AptoideApplication) getContext().getApplicationContext());
    attachPresenter(
        new InboxPresenter(this, ((ActivityResultNavigator) getContext()).getInboxNavigator(),
            ((AptoideApplication) getContext().getApplicationContext()).getNotificationCenter(),
            CrashReport.getInstance(),
            ((AptoideApplication) getContext().getApplicationContext()).getNavigationTracker(),
            application.getNotificationAnalytics(),
            AndroidSchedulers.mainThread()));
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    bottomNavigationActivity.hide();
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override public void showNotifications(List<AptoideNotification> notifications) {
    adapter.updateNotifications(notifications);
  }

  @Override public Observable<AptoideNotification> notificationSelection() {
    return notificationSubject;
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_inbox;
  }
}
