package cm.aptoide.pt.v8engine.social;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.timeline.PackageRepository;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.fragment.FragmentView;
import java.util.Collections;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 31/05/2017.
 */

public class TimelineFragment extends FragmentView implements TimelineView {

  private static final String ACTION_KEY = "action";
  private RecyclerView list;
  private CardAdapter adapter;
  private PublishSubject<Article> articleSubject;
  private String url;

  public static Fragment newInstance(String action, Long userId, Long storeId,
      StoreContext storeContext) {
    final Bundle args = new Bundle();
    Fragment fragment = new TimelineFragment();
    args.putString(ACTION_KEY, action);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loadExtras();
    attachPresenter(new TimelinePresenter(this, new SocialManager(new SocialService(url,
        ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7(),
        ((V8Engine) getContext().getApplicationContext()).getDefaultClient(),
        WebService.getDefaultConverter(), new PackageRepository(getContext().getPackageManager()),
        20, 10, new TimelineResponseCardMapper()))), savedInstanceState);
    articleSubject = PublishSubject.create();
    adapter = new CardAdapter(Collections.emptyList(), articleSubject, new DateCalculator());
  }

  private void loadExtras() {
    if (getArguments() != null) {
      url = getArguments().getString(ACTION_KEY);
    }
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_timeline, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    list = (RecyclerView) view.findViewById(R.id.fragment_cards_list);
    list.setAdapter(adapter);
    list.setLayoutManager(new LinearLayoutManager(getContext()));
  }

  @Override public void showCards(List<Article> cards) {
    adapter.updateCards(cards);
  }
}
