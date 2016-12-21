package cm.aptoide.pt.v8engine.fragment.implementations;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.dataprovider.ws.v7.ListCommentsRequest;
import cm.aptoide.pt.model.v7.ListComments;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragment;
import cm.aptoide.pt.v8engine.util.CommentOperations;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayableGroup;
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

public class CommentListFragment extends GridRecyclerFragment {

  private static final String TAG = StoreGridRecyclerFragment.class.getName();

  //
  // consts
  //
  private static final String COMMENT_TYPE = "comment_type";
  private static final String ELEMENT_ID = "element_id";
  private static final String URL_VAL = "url_val";

  //
  // vars
  //
  private CommentOperations commentOperations;
  private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
  private List<Displayable> displayables;
  private CommentType commentType;
  private String elementId;
  private String url;

  //
  // views
  //
  private FloatingActionButton floatingActionButton;

  public static Fragment newInstance(CommentType commentType, String elementId) {
    Bundle args = new Bundle();
    args.putString(ELEMENT_ID, elementId);
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

  @Override public void setupToolbar() {
    // It's not calling super cause it does nothing in the middle class}
    // StoreTabGridRecyclerFragment.
    if (toolbar != null) {
      ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
      ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.comments);
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

  @Override public void setupViews() {
    super.setupViews();
    setupToolbar();
    setHasOptionsMenu(true);

    // FIXME: 20/12/2016 sithengineer refactor this

    RxView.clicks(floatingActionButton)
        .flatMap(a -> createNewCommentFragment(elementId))
        .compose(bindUntilEvent(LifecycleEvent.DESTROY_VIEW))
        .subscribe(a -> {
          // no-op
        });

    caseListSocialTimelineComments(true);
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fabAdd);
    if (floatingActionButton != null) {
      floatingActionButton.setVisibility(View.VISIBLE);
    }
    commentOperations = new CommentOperations();
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    elementId = args.getString(ELEMENT_ID);
    url = args.getString(URL_VAL);
    commentType = Enum.valueOf(CommentType.class, args.getString(COMMENT_TYPE));
  }

  private Observable<Void> reloadComments() {
    return Observable.fromCallable(() -> {
      ManagerPreferences.setForceServerRefreshFlag(true);
      //super.reload();
      return null;
    });
  }

  private Observable<Void> showSignInMessage() {
    return ShowMessage.asObservableSnack(this.getActivity(), R.string.you_need_to_be_logged_in,
        R.string.login, snackView -> {
          //AptoideAccountManager.openAccountManager(StoreGridRecyclerFragment.this.getContext());
        }).flatMap(a -> Observable.empty());
  }

  //
  // create new comment different fragment constructions
  //

  // Used method for each single reply in the list view and the new comment button
  public Observable<Void> createNewCommentFragment(final String timelineArticleId) {

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
      final long commentId) {

    return Observable.just(AptoideAccountManager.isLoggedIn()).flatMap(isLoggedIn -> {

      if (isLoggedIn) {
        // show fragment CommentDialog
        FragmentManager fm = CommentListFragment.this.getActivity().getFragmentManager();
        CommentDialogFragment commentDialogFragment =
            CommentDialogFragment.newInstanceTimelineArticleComment(timelineArticleId, commentId);

        return commentDialogFragment.lifecycle()
            .doOnSubscribe(() -> commentDialogFragment.show(fm, "fragment_comment_dialog"))
            .filter(event -> event.equals(FragmentEvent.DESTROY_VIEW))
            .flatMap(event -> reloadComments());
      }

      return showSignInMessage();
    });
  }

  /*
  public Observable<Void> createNewCommentFragment(final long storeId, final long commentId,
      String storeName) {

    return Observable.just(AptoideAccountManager.isLoggedIn()).flatMap(isLoggedIn -> {

      if (isLoggedIn) {
        // show fragment CommentDialog
        FragmentManager fm = CommentListFragment.this.getActivity().getFragmentManager();
        CommentDialogFragment commentDialogFragment =
            CommentDialogFragment.newInstanceStoreCommentReply(storeId, commentId, storeName);

        return commentDialogFragment.lifecycle()
            .doOnSubscribe(() -> commentDialogFragment.show(fm, "fragment_comment_dialog"))
            .filter(event -> event.equals(FragmentEvent.DESTROY_VIEW))
            .flatMap(event -> reloadComments());
      }

      return showSignInMessage();
    });
  }
  */

  void caseListSocialTimelineComments(boolean refresh) {

    String aptoideClientUuid = new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
        DataProvider.getContext()).getAptoideClientUUID();

    ListCommentsRequest listCommentsRequest =
        ListCommentsRequest.ofTimeline(url, refresh, elementId,
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
              createNewCommentFragment(elementId, commentNode.getComment().getId()))));
        }

        this.displayables = new ArrayList<>(displayables.size());
        this.displayables.add(new DisplayableGroup(displayables));

        addDisplayables(this.displayables);
      }
    });
    recyclerView.clearOnScrollListeners();
    endlessRecyclerOnScrollListener =
        new EndlessRecyclerOnScrollListener(getAdapter(), listCommentsRequest, listCommentsAction,
            errorRequestListener, true);

    recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
    endlessRecyclerOnScrollListener.onLoadMore(refresh);
  }

  /*
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
        this.displayables.add(new DisplayableGroup(displayables));

        addDisplayables(this.displayables);
      }
    });
    recyclerView.clearOnScrollListeners();
    endlessRecyclerOnScrollListener =
        new EndlessRecyclerOnScrollListener(getAdapter(), listCommentsRequest, listCommentsAction,
            errorRequestListener, true);

    recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
    endlessRecyclerOnScrollListener.onLoadMore(refresh);
  }
  */
}
