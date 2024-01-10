/*
 * Copyright (c) 2016.
 * Modified on 02/08/2016.
 */

package cm.aptoide.pt.store.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.model.v7.Layout;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.home.bundles.base.HomeEvent;
import cm.aptoide.pt.store.RoomStoreRepository;
import cm.aptoide.pt.view.MainActivity;
import cm.aptoide.pt.view.Translator;
import cm.aptoide.pt.view.fragment.DisplayableManager;
import cm.aptoide.pt.view.fragment.GridRecyclerSwipeFragment;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Observable;

/**
 * Created by neuro on 29-04-2016.
 */
public abstract class StoreTabGridRecyclerFragment extends GridRecyclerSwipeFragment {

  protected Event.Type type;
  protected HomeEvent.Type homeEventType;
  protected Event.Name name;
  protected Layout layout;
  protected String action;
  protected String title;
  protected String tag;
  protected String storeTheme;
  protected StoreContext storeContext;
  private boolean isESkills;
  @Inject RoomStoreRepository storeRepository;
  @Inject @Named("marketName") String marketName;

  public static Fragment newInstance(Event event, String storeTheme, String tag,
      StoreContext storeContext, boolean shouldShowToolbar) {
    return newInstance(event, null, storeTheme, tag, storeContext, shouldShowToolbar);
  }

  public static Fragment newInstance(Event event, String title, String storeTheme, String tag,
      StoreContext storeContext, boolean shouldShowToolbar) {
    return newInstance(event, HomeEvent.Type.NO_OP, title, storeTheme, tag, storeContext,
        shouldShowToolbar);
  }

  public static Fragment newInstance(Event event, HomeEvent.Type type, String title,
      String storeTheme, String tag, StoreContext storeContext, boolean shouldShowToolbar) {
    Bundle args = buildBundle(event, type, title, storeTheme, tag, storeContext, shouldShowToolbar);
    Fragment fragment = StoreTabFragmentChooser.choose(event, type);
    Bundle arguments = fragment.getArguments();
    if (arguments != null) {
      args.putAll(arguments);
    }
    fragment.setArguments(args);
    return fragment;
  }
  @NonNull
  protected static Bundle buildBundle(Event event, HomeEvent.Type homeEventType, String title,
      String storeTheme, String tag, StoreContext storeContext, boolean shouldShowToolbar) {
    Bundle args = new Bundle();

    if (homeEventType != null) {
      args.putString(BundleCons.HOME_EVENT_TYPE, homeEventType.toString());
    }

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
    args.putBoolean(BundleCons.IS_ESKILLS, homeEventType == HomeEvent.Type.ESKILLS);
    args.putString(BundleCons.TITLE, title);
    args.putString(BundleCons.ACTION, event.getAction());
    args.putString(BundleCons.STORE_THEME, storeTheme);
    args.putString(BundleCons.TAG, tag);
    args.putBoolean(BundleCons.TOOLBAR, shouldShowToolbar);
    return args;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    ((MainActivity) getContext()).getActivityComponent()
        .inject(this);

    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);

  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName(), tag, storeContext != null ? storeContext.name() : null);
  }

  @Override public void loadExtras(Bundle args) {
    if (args.containsKey(BundleCons.HOME_EVENT_TYPE)) {
      homeEventType = HomeEvent.Type.valueOf(args.getString(BundleCons.HOME_EVENT_TYPE));
    }
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
    isESkills = args.getBoolean(BundleCons.IS_ESKILLS);
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
      Observable<List<Displayable>> displayablesObservable =
          buildDisplayables(refresh, url, refresh);
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
  protected abstract Observable<List<Displayable>> buildDisplayables(boolean refresh, String url,
      boolean bypassServerCache);

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
    if(!isESkills) {
      toolbar.setLogo(R.drawable.logo_toolbar);
    }

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
  }

  public boolean isEskills() {
    return isESkills;
  }

  public static class BundleCons {

    public static final String TYPE = "type";
    public static final String HOME_EVENT_TYPE = "HomeEventType";
    public static final String NAME = "name";
    public static final String TITLE = "title";
    public static final String ACTION = "action";
    public static final String STORE_THEME = "storeTheme";
    public static final String LAYOUT = "layout";
    public static final String TAG = "tag";
    public static final String STORE_NAME = "store_name";
    public static final String TOOLBAR = "toolbar";
    public static final String GROUP_ID = "group_id";
    public static String STORE_CONTEXT = "Store_context";
    public static final String IS_ESKILLS = "is_eskills";

  }
}
