package cm.aptoide.pt.v8engine.fragment;

import android.app.FragmentManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.ListCommentsRequest;
import cm.aptoide.pt.model.v7.ListComments;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.util.CommentOperations;
import cm.aptoide.pt.v8engine.util.StoreUtils;
import cm.aptoide.pt.v8engine.view.custom.HorizontalDividerItemDecoration;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.CommentsDisplayableGroup;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.DisplayableGroupWithMargin;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CommentDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.listeners.EndlessRecyclerOnScrollListener;
import cm.aptoide.pt.viewRateAndCommentReviews.CommentDialogFragment;
import cm.aptoide.pt.viewRateAndCommentReviews.CommentNode;
import cm.aptoide.pt.viewRateAndCommentReviews.ComplexComment;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.FragmentEvent;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.functions.Action1;

// TODO: 21/12/2016 sithengineer refactor and split in multiple classes to list comments
// for each type: store and timeline card
public class CommentListFragment extends GridRecyclerSwipeFragment {

  //private static final String TAG = StoreGridRecyclerFragment.class.getName();

  //
  // consts
  //
  private static final String COMMENT_TYPE = "comment_type";
  private static final String ELEMENT_ID_AS_STRING = "element_id_as_string";
  private static final String ELEMENT_ID_AS_LONG = "element_id_as_long";
  private static final String URL_VAL = "url_val";

  //
  // vars
  //
  private CommentOperations commentOperations;
  private List<Displayable> displayables;
  private CommentType commentType;
  private String url;
  // timeline card comments vars
  private String elementIdAsString;
  // store comments vars
  private long elementIdAsLong;
  private String storeName;

  //
  // views
  //
  private FloatingActionButton floatingActionButton;

  public static Fragment newInstance(CommentType commentType, String timelineArticleId) {
    Bundle args = new Bundle();
    args.putString(ELEMENT_ID_AS_STRING, timelineArticleId);
    args.putString(COMMENT_TYPE, commentType.name());

    CommentListFragment fragment = new CommentListFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public static Fragment newInstanceUrl(CommentType commentType, String url) {
    Bundle args = new Bundle();
    args.putString(URL_VAL, url);
    args.putString(COMMENT_TYPE, commentType.name());

    CommentListFragment fragment = new CommentListFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    elementIdAsString = args.getString(ELEMENT_ID_AS_STRING);
    elementIdAsLong = args.getLong(ELEMENT_ID_AS_LONG);
    url = args.getString(URL_VAL);
    commentType = CommentType.valueOf(args.getString(COMMENT_TYPE));

    // extracting store data from the URL...
    if (commentType == CommentType.STORE) {
      BaseRequestWithStore.StoreCredentials storeCredentials =
          StoreUtils.getStoreCredentialsFromUrlOrNull(url);
      if (storeCredentials != null) {

        Long id = storeCredentials.getId();
        if (id != null) {
          elementIdAsLong = id;
        }

        if (!TextUtils.isEmpty(storeCredentials.getName())) {
          storeName = storeCredentials.getName();
        }
      }
    }
  }

  @Override public void setupToolbar() {
    // It's not calling super cause it does nothing in the middle class}
    // StoreTabGridRecyclerFragment.
    if (toolbar != null) {
      ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
      if (commentType == CommentType.STORE && !TextUtils.isEmpty(storeName)) {
        String title = String.format(getString(R.string.comment_on_store), storeName);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
      } else {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.comments);
      }
      ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      toolbar.setLogo(R.drawable.ic_aptoide_toolbar);
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

  @Override public int getContentViewId() {
    return R.layout.recycler_swipe_fragment_with_toolbar;
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);
    if (create || refresh) {
      refreshData();
    }
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    commentOperations = new CommentOperations();
    floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fabAdd);
    if (floatingActionButton != null) {
      Drawable drawable;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        drawable = getContext().getDrawable(R.drawable.forma_1);
      } else {
        drawable = getActivity().getResources().getDrawable(R.drawable.forma_1);
      }
      floatingActionButton.setImageDrawable(drawable);
      floatingActionButton.setVisibility(View.VISIBLE);
    }
  }

  @Override protected RecyclerView.ItemDecoration getItemDecoration() {
    return new HorizontalDividerItemDecoration(getContext(), 0);
  }

  @Override public void setupViews() {
    super.setupViews();
    setupToolbar();
    setHasOptionsMenu(true);

    RxView.clicks(floatingActionButton).flatMap(a -> {
      if (commentType == CommentType.TIMELINE) {
        return createNewCommentFragment(elementIdAsString);
      }
      return createNewCommentFragment(elementIdAsLong, storeName);
    }).compose(bindUntilEvent(LifecycleEvent.DESTROY_VIEW)).subscribe(a -> {
      // no-op
    });
  }

  void refreshData() {
    if (commentType == CommentType.TIMELINE) {
      caseListSocialTimelineComments(true);
    } else {
      caseListStoreComments(url, StoreUtils.getStoreCredentialsFromUrl(url), true);
    }
  }

  private Observable<Void> reloadComments() {
    return Observable.fromCallable(() -> {
      ManagerPreferences.setForceServerRefreshFlag(true);
      super.reload();
      return null;
    });
  }

  private Observable<Void> showSignInMessage() {
    return ShowMessage.asObservableSnack(this.getActivity(), R.string.you_need_to_be_logged_in,
        R.string.login, snackView -> {
          AptoideAccountManager.openAccountManager(CommentListFragment.this.getContext());
        }).flatMap(a -> Observable.empty());
  }

  //
  // Re-Do: 6/1/2017 sithengineer create new comment different fragment constructions
  //

  //
  // Timeline Articles comments methods
  //

  public Observable<Void> createNewCommentFragment(String timelineArticleId) {

    return Observable.just(AptoideAccountManager.isLoggedIn()).flatMap(isLoggedIn -> {

      if (isLoggedIn) {
        // show fragment CommentDialog
        FragmentManager fm = CommentListFragment.this.getActivity().getFragmentManager();
        CommentDialogFragment commentDialogFragment =
            CommentDialogFragment.newInstanceTimelineArticleComment(timelineArticleId);

        return commentDialogFragment.lifecycle()
            .doOnSubscribe(() -> commentDialogFragment.show(fm, "fragment_comment_dialog"))
            .filter(event -> event.equals(FragmentEvent.DESTROY_VIEW))
            .flatMap(event -> reloadComments());
      }

      return showSignInMessage();
    });
  }

  public Observable<Void> createNewCommentFragment(final String timelineArticleId,
      final long previousCommentId) {

    return Observable.just(AptoideAccountManager.isLoggedIn()).flatMap(isLoggedIn -> {

      if (isLoggedIn) {
        // show fragment CommentDialog
        FragmentManager fm = CommentListFragment.this.getActivity().getFragmentManager();
        CommentDialogFragment commentDialogFragment =
            CommentDialogFragment.newInstanceTimelineArticleComment(timelineArticleId,
                previousCommentId);

        return commentDialogFragment.lifecycle()
            .doOnSubscribe(() -> commentDialogFragment.show(fm, "fragment_comment_dialog"))
            .filter(event -> event.equals(FragmentEvent.DESTROY_VIEW))
            .flatMap(event -> reloadComments());
      }

      return showSignInMessage();
    });
  }

  void caseListSocialTimelineComments(boolean refresh) {

    String aptoideClientUuid = new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
        DataProvider.getContext()).getAptoideClientUUID();

    ListCommentsRequest listCommentsRequest =
        ListCommentsRequest.ofTimeline(url, refresh, elementIdAsString,
            AptoideAccountManager.getAccessToken(), aptoideClientUuid);

    Action1<ListComments> listCommentsAction = (listComments -> {
      if (listComments != null
          && listComments.getDatalist() != null
          && listComments.getDatalist().getList() != null) {
        List<CommentNode> comments = commentOperations.flattenByDepth(
            commentOperations.transform(listComments.getDatalist().getList()));

        ArrayList<Displayable> displayables = new ArrayList<>(comments.size());
        for (CommentNode commentNode : comments) {
          displayables.add(new CommentDisplayable(new ComplexComment(commentNode,
              createNewCommentFragment(elementIdAsString, commentNode.getComment().getId()))));
        }

        this.displayables = new ArrayList<>(displayables.size());
        this.displayables.add(new DisplayableGroupWithMargin(displayables));

        addDisplayables(this.displayables);
      }
    });
    recyclerView.clearOnScrollListeners();
    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener =
        new EndlessRecyclerOnScrollListener(getAdapter(), listCommentsRequest, listCommentsAction,
            Throwable::printStackTrace, true);

    recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
    endlessRecyclerOnScrollListener.onLoadMore(refresh);
  }

  //
  // Store comments methods
  //

  public Observable<Void> createNewCommentFragment(long storeCommentId, String storeName) {

    return Observable.just(AptoideAccountManager.isLoggedIn()).flatMap(isLoggedIn -> {

      if (isLoggedIn) {
        // show fragment CommentDialog
        FragmentManager fm = CommentListFragment.this.getActivity().getFragmentManager();
        CommentDialogFragment commentDialogFragment =
            CommentDialogFragment.newInstanceStoreComment(storeCommentId, storeName);

        return commentDialogFragment.lifecycle()
            .doOnSubscribe(() -> commentDialogFragment.show(fm, "fragment_comment_dialog"))
            .filter(event -> event.equals(FragmentEvent.DESTROY_VIEW))
            .flatMap(event -> reloadComments());
      }

      return showSignInMessage();
    });
  }

  private Observable<Void> createNewCommentFragment(long storeId, long previousCommentId,
      String storeName) {

    return Observable.just(AptoideAccountManager.isLoggedIn()).flatMap(isLoggedIn -> {

      if (isLoggedIn) {
        // show fragment CommentDialog
        FragmentManager fm = CommentListFragment.this.getActivity().getFragmentManager();
        CommentDialogFragment commentDialogFragment =
            CommentDialogFragment.newInstanceStoreCommentReply(storeId, previousCommentId,
                storeName);

        return commentDialogFragment.lifecycle()
            .doOnSubscribe(() -> commentDialogFragment.show(fm, "fragment_comment_dialog"))
            .filter(event -> event.equals(FragmentEvent.DESTROY_VIEW))
            .flatMap(event -> reloadComments());
      }

      return showSignInMessage();
    });
  }

  void caseListStoreComments(String url, BaseRequestWithStore.StoreCredentials storeCredentials,
      boolean refresh) {

    String aptoideClientUuid = new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
        DataProvider.getContext()).getAptoideClientUUID();

    ListCommentsRequest listCommentsRequest =
        ListCommentsRequest.ofStoreAction(url, refresh, storeCredentials,
            AptoideAccountManager.getAccessToken(), aptoideClientUuid);

    if (storeCredentials.getId() == null) {
      CrashReports.logException(
          new IllegalStateException("Current store credentials does not have a store id"));
    }

    final long storeId = storeCredentials.getId() != null ? storeCredentials.getId() : -1;
    final String storeName = storeCredentials.getName();

    Action1<ListComments> listCommentsAction = (listComments -> {
      if (listComments != null
          && listComments.getDatalist() != null
          && listComments.getDatalist().getList() != null) {
        List<CommentNode> comments = commentOperations.flattenByDepth(
            commentOperations.transform(listComments.getDatalist().getList()));

        ArrayList<Displayable> displayables = new ArrayList<>(comments.size());
        for (CommentNode commentNode : comments) {
          displayables.add(new CommentDisplayable(new ComplexComment(commentNode,
              createNewCommentFragment(storeId, commentNode.getComment().getId(), storeName))));
        }

        this.displayables = new ArrayList<>(displayables.size());
        this.displayables.add(new CommentsDisplayableGroup(displayables));

        addDisplayables(this.displayables);
      }
    });

    // remove recycler view left and right padding
    recyclerView.setPadding(0, recyclerView.getPaddingTop(), 0, recyclerView.getPaddingBottom());

    recyclerView.clearOnScrollListeners();
    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener =
        new EndlessRecyclerOnScrollListener(getAdapter(), listCommentsRequest, listCommentsAction,
            Throwable::printStackTrace, true);

    recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
    endlessRecyclerOnScrollListener.onLoadMore(refresh);
  }
}
