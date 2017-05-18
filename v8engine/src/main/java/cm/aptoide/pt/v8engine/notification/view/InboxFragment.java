package cm.aptoide.pt.v8engine.notification.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.link.LinksHandlerFactory;
import cm.aptoide.pt.v8engine.notification.AptoideNotification;
import cm.aptoide.pt.v8engine.view.fragment.FragmentView;
import java.util.Collections;
import java.util.List;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by pedroribeiro on 16/05/17.
 */

public class InboxFragment extends FragmentView implements InboxView {

  private RecyclerView list;
  private InboxAdapter adapter;

  private PublishSubject<AptoideNotification> notificationSubject;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    attachPresenter(new InboxPresenter(this,
        ((V8Engine) getContext().getApplicationContext()).getNotificationCenter(),
        new LinksHandlerFactory(getContext()), this.getArguments()
        .getInt("numberOfNotifications")), savedInstanceState);
    notificationSubject = PublishSubject.create();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_inbox, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    list = (RecyclerView) view.findViewById(R.id.fragment_inbox_list);
    adapter = new InboxAdapter(Collections.emptyList(), notificationSubject);
    super.onViewCreated(view, savedInstanceState);
    if (this.getArguments()
        .getBoolean("showToolbar")) {
      setupToolbar(view);
    } else {
      setupToolbar(view).setVisibility(View.GONE);
    }
    list.setAdapter(adapter);
    list.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
  }

  @Override public void showNotifications(List<AptoideNotification> notifications) {
    adapter.updateNotifications(notifications);
  }

  @Override public Observable<AptoideNotification> notificationSelection() {
    return notificationSubject;
  }

  private Toolbar setupToolbar(View view) {
    Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    toolbar.setLogo(R.drawable.logo_toolbar);
    toolbar.setTitle(R.string.notification_center_title);
    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);

    return toolbar;
  }
}
