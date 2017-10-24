/*
 * Copyright (c) 2016.
 * Modified on 02/08/2016.
 */

package cm.aptoide.pt.view.store;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.model.v7.Layout;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.repository.StoreRepository;
import cm.aptoide.pt.view.Translator;
import cm.aptoide.pt.view.fragment.DisplayableManager;
import cm.aptoide.pt.view.fragment.GridRecyclerSwipeFragment;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import java.util.List;
import rx.Observable;

/**
 * Created by neuro on 29-04-2016.
 */
public abstract class StoreTabGridRecyclerFragment extends GridRecyclerSwipeFragment {

  protected StoreRepository storeRepository;

  protected Event.Type type;
  protected Event.Name name;
  protected Layout layout;
  protected String action;
  protected String title;
  protected String tag;
  protected String storeTheme;
  protected StoreContext storeContext;
  private String marketName;

  public static Fragment newInstance(Event event, String storeTheme, String tag,
      StoreContext storeContext) {
    return newInstance(event, null, storeTheme, tag, storeContext);
  }

  public static Fragment newInstance(Event event, String title, String storeTheme, String tag,
      StoreContext storeContext) {
    Bundle args = buildBundle(event, title, storeTheme, tag, storeContext);
    Fragment fragment = StoreTabFragmentChooser.choose(event.getName());
    Bundle arguments = fragment.getArguments();
    if (arguments != null) {
      args.putAll(arguments);
    }
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull
  protected static Bundle buildBundle(Event event, String title, String storeTheme, String tag,
      StoreContext storeContext) {
    Bundle args = new Bundle();

    if (event.getType() != null) {
      args.putString(BundleCons.TYPE, event.getType()
          .toString());
    }

    if (event.getName() != null) {
      args.putString(BundleCons.NAME, event.getName()
          .toString());
    }

    if (event.getData() != null
        && event.getData()
        .getLayout() != null) {
      args.putString(BundleCons.LAYOUT, event.getData()
          .getLayout()
          .toString());
    }

    if (storeContext != null) {
      args.putSerializable(BundleCons.STORE_CONTEXT, storeContext);
    }

    args.putString(BundleCons.TITLE, title);
    args.putString(BundleCons.ACTION, event.getAction());
    args.putString(BundleCons.STORE_THEME, storeTheme);
    args.putString(BundleCons.TAG, tag);
    return args;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    marketName = ((AptoideApplication) getContext().getApplicationContext()).getMarketName();
    storeRepository = RepositoryFactory.getStoreRepository(getContext().getApplicationContext());

    super.onCreate(savedInstanceState);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName(), tag, storeContext);
  }

  @Override public void loadExtras(Bundle args) {
    if (args.containsKey(BundleCons.TYPE)) {
      type = Event.Type.valueOf(args.getString(BundleCons.TYPE));
    }
    if (args.containsKey(BundleCons.NAME)) {
      name = Event.Name.valueOf(args.getString(BundleCons.NAME));
    }
    if (args.containsKey(BundleCons.LAYOUT)) {
      layout = Layout.valueOf(args.getString(BundleCons.LAYOUT));
    }
    if (args.containsKey(BundleCons.TAG)) {
      tag = args.getString(BundleCons.TAG);
    }
    if (args.containsKey(BundleCons.STORE_CONTEXT)) {
      storeContext = ((StoreContext) args.getSerializable(BundleCons.STORE_CONTEXT));
    }
    title = args.getString(
        Translator.translate(BundleCons.TITLE, getContext().getApplicationContext(), marketName));
    action = args.getString(BundleCons.ACTION);
    storeTheme = args.getString(BundleCons.STORE_THEME);
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);
    if (create || refresh || !hasDisplayables()) {
      String url = action != null ? action.replace(V7.getHost(
          ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences()),
          "") : null;

      if (!StoreTabFragmentChooser.validateAcceptedName(name)) {
        throw new RuntimeException(
            "Invalid name(" + name + ") for event on " + getClass().getSimpleName() + "!");
      }

      // TODO: 28-12-2016 neuro martelo martelo martelo
      Observable<List<Displayable>> displayablesObservable = buildDisplayables(refresh, url);
      if (displayablesObservable != null) {
        DisplayableManager displayableManager = this;
        displayablesObservable.compose(bindUntilEvent(LifecycleEvent.DESTROY))
            .subscribe(displayables -> {
              displayableManager.clearDisplayables()
                  .addDisplayables(displayables, true);
            }, err -> {
              CrashReport.getInstance()
                  .log(err);
              StoreTabGridRecyclerFragment.this.finishLoading(err);
            });
      }
    }
  }

  @Nullable
  protected abstract Observable<List<Displayable>> buildDisplayables(boolean refresh, String url);

  @Override public int getContentViewId() {
    // title flag whether toolbar should be shown or not
    if (title != null) {
      return R.layout.recycler_swipe_fragment_with_toolbar;
    } else {
      return super.getContentViewId();
    }
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override public void setupToolbarDetails(Toolbar toolbar) {
    toolbar.setTitle(Translator.translate(title, getContext().getApplicationContext(), marketName));
    toolbar.setLogo(R.drawable.logo_toolbar);
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_empty, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void setupViews() {
    super.setupViews();
    setupToolbar();
    setHasOptionsMenu(true);
  }

  private static class BundleCons {

    public static final String TYPE = "type";
    public static final String NAME = "name";
    public static final String TITLE = "title";
    public static final String ACTION = "action";
    public static final String STORE_THEME = "storeTheme";
    public static final String LAYOUT = "layout";
    public static final String TAG = "tag";
    public static final String STORE_NAME = "store_name";
    public static String STORE_CONTEXT = "Store_context";
  }
}
