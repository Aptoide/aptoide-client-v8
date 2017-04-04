package cm.aptoide.pt.v8engine.view.comments;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.ListCommentsRequest;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.ListComments;
import cm.aptoide.pt.model.v7.SetComment;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.account.AccountNavigator;
import cm.aptoide.pt.v8engine.view.fragment.GridRecyclerSwipeFragment;
import cm.aptoide.pt.v8engine.interfaces.CommentDialogCallbackContract;
import cm.aptoide.pt.v8engine.interfaces.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.util.CommentOperations;
import cm.aptoide.pt.v8engine.util.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.util.StoreUtils;
import cm.aptoide.pt.v8engine.view.custom.HorizontalDividerItemDecoration;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayableGroup;
import cm.aptoide.pt.v8engine.view.recycler.EndlessRecyclerOnScrollListener;
import cm.aptoide.pt.v8engine.comments.CommentNode;
import cm.aptoide.pt.v8engine.comments.ComplexComment;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import rx.Observable;
import rx.functions.Action1;

// TODO: 21/12/2016 sithengineer refactor and split in multiple classes to list comments
// for each type: store and timeline card
public class CommentListFragment extends GridRecyclerSwipeFragment
    implements CommentDialogCallbackContract {

  //
  // consts
  //
  private static final String COMMENT_TYPE = "comment_type";
  private static final String ELEMENT_ID_AS_STRING = "element_id_as_string";
  private static final String ELEMENT_ID_AS_LONG = "element_id_as_long";
  private static final String URL_VAL = "url_val";
  // control setComment retry
  protected long lastTotal;
  //
  // vars
  //
  private CommentOperations commentOperations;
  private List<Displayable> displayables;
  private CommentType commentType;
  private String url;
  // timeline card comments vars
  private String elementIdAsString;
  private List<CommentNode> comments;
  // store comments vars
  private long elementIdAsLong;
  private String storeName;
  //
  // views
  //
  private FloatingActionButton floatingActionButton;
  private AptoideAccountManager accountManager;
  private AccountNavigator accountNavigator;
  private BodyInterceptor<BaseBody> bodyDecorator;
  private StoreCredentialsProvider storeCredentialsProvider;

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

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    //this object is used in loadExtras and loadExtras is called in the super
    storeCredentialsProvider = new StoreCredentialsProviderImpl();
    super.onCreate(savedInstanceState);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View v = super.onCreateView(inflater, container, savedInstanceState);
    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    bodyDecorator = ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
    accountNavigator =
        new AccountNavigator(getFragmentNavigator(), accountManager, getActivityNavigator());
    return v;
  }

  @Override protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override public void setupToolbarDetails(Toolbar toolbar) {
    if (commentType == CommentType.STORE && !TextUtils.isEmpty(storeName)) {
      String title = String.format(getString(R.string.comment_on_store), storeName);
      toolbar.setTitle(title);
    } else {
      toolbar.setTitle(R.string.comments);
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

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    elementIdAsString = args.getString(ELEMENT_ID_AS_STRING);
    elementIdAsLong = args.getLong(ELEMENT_ID_AS_LONG);
    url = args.getString(URL_VAL);
    commentType = CommentType.valueOf(args.getString(COMMENT_TYPE));

    // extracting store data from the URL...
    if (commentType == CommentType.STORE) {

      BaseRequestWithStore.StoreCredentials storeCredentials =
          StoreUtils.getStoreCredentialsFromUrl(url, storeCredentialsProvider);

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

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);
    if (create || refresh) {
      refreshData();
    }
  }

  void refreshData() {
    if (commentType == CommentType.TIMELINE) {
      caseListSocialTimelineComments(true);
    } else {
      caseListStoreComments(url,
          StoreUtils.getStoreCredentialsFromUrl(url, storeCredentialsProvider), true);
    }
  }

  void caseListSocialTimelineComments(boolean refresh) {
    ListCommentsRequest listCommentsRequest =
        ListCommentsRequest.ofTimeline(url, refresh, elementIdAsString, bodyDecorator);

    Action1<ListComments> listCommentsAction = (listComments -> {
      if (listComments != null
          && listComments.getDatalist() != null
          && listComments.getDatalist().getList() != null) {
        comments = commentOperations.flattenByDepth(
            commentOperations.transform(listComments.getDatalist().getList()));

        ArrayList<Displayable> displayables = new ArrayList<>(comments.size());
        for (CommentNode commentNode : comments) {
          displayables.add(new CommentDisplayable(new ComplexComment(commentNode,
              createNewCommentFragment(elementIdAsString, commentNode.getComment().getId()))));
        }

        this.displayables = new ArrayList<>(displayables.size());
        this.displayables.add(new DisplayableGroup(displayables));

        addDisplayables(this.displayables);
      }
    });
    getRecyclerView().clearOnScrollListeners();
    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener =
        new EndlessRecyclerOnScrollListener(getAdapter(), listCommentsRequest, listCommentsAction,
            err -> err.printStackTrace(), true);

    getRecyclerView().addOnScrollListener(endlessRecyclerOnScrollListener);
    endlessRecyclerOnScrollListener.onLoadMore(refresh);
  }

  void caseListStoreComments(String url, BaseRequestWithStore.StoreCredentials storeCredentials,
      boolean refresh) {

    ListCommentsRequest listCommentsRequest =
        ListCommentsRequest.ofStoreAction(url, refresh, storeCredentials, bodyDecorator);

    if (storeCredentials == null || storeCredentials.getId() == null) {
      IllegalStateException illegalStateException =
          new IllegalStateException("Current store credentials does not have a store id");
      CrashReport.getInstance().log(illegalStateException);
      throw illegalStateException;
    }

    final long storeId = storeCredentials.getId() != null ? storeCredentials.getId() : -1;
    final String storeName = storeCredentials.getName();

    Action1<ListComments> listCommentsAction = (listComments -> {
      if (listComments != null
          && listComments.getDatalist() != null
          && listComments.getDatalist().getList() != null) {
        comments = commentOperations.flattenByDepth(
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

    getRecyclerView().clearOnScrollListeners();
    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener =
        new EndlessRecyclerOnScrollListener(getAdapter(), listCommentsRequest, listCommentsAction,
            err -> err.printStackTrace(), true);

    getRecyclerView().addOnScrollListener(endlessRecyclerOnScrollListener);
    endlessRecyclerOnScrollListener.onLoadMore(refresh);
  }

  public Observable<Void> createNewCommentFragment(final String timelineArticleId,
      final long previousCommentId) {

    return Observable.just(accountManager.isLoggedIn()).flatMap(isLoggedIn -> {

      if (isLoggedIn) {
        // show fragment CommentDialog
        FragmentManager fm = CommentListFragment.this.getActivity().getSupportFragmentManager();
        CommentDialogFragment commentDialogFragment =
            CommentDialogFragment.newInstanceTimelineArticleComment(timelineArticleId,
                previousCommentId);
        commentDialogFragment.setCommentDialogCallbackContract(this);

        return commentDialogFragment.lifecycle()
            .doOnSubscribe(() -> commentDialogFragment.show(fm, "fragment_comment_dialog"))
            .filter(event -> event.equals(FragmentEvent.DESTROY_VIEW))
            .flatMap(event -> Observable.empty());
      }

      return showSignInMessage();
    });
  }

  private Observable<Void> createNewCommentFragment(long storeId, long previousCommentId,
      String storeName) {

    return Observable.just(accountManager.isLoggedIn()).flatMap(isLoggedIn -> {

      if (isLoggedIn) {
        // show fragment CommentDialog
        FragmentManager fm = CommentListFragment.this.getActivity().getSupportFragmentManager();
        CommentDialogFragment commentDialogFragment =
            CommentDialogFragment.newInstanceStoreCommentReply(storeId, previousCommentId,
                storeName);
        commentDialogFragment.setCommentDialogCallbackContract(this);

        return commentDialogFragment.lifecycle()
            .doOnSubscribe(() -> commentDialogFragment.show(fm, "fragment_comment_dialog"))
            .filter(event -> event.equals(FragmentEvent.DESTROY_VIEW))
            .flatMap(event -> Observable.empty());
      }

      return showSignInMessage();
    });
  }

  //
  // Re-Do: 6/1/2017 sithengineer create new comment different fragment constructions
  //

  //
  // Timeline Articles comments methods
  //

  private Observable<Void> showSignInMessage() {
    return ShowMessage.asObservableSnack(this.getActivity(), R.string.you_need_to_be_logged_in,
        R.string.login, snackView -> {
          accountNavigator.navigateToAccountView();
        }).flatMap(a -> Observable.empty());
  }

  private Observable<Void> reloadComments() {
    return Observable.fromCallable(() -> {
      ManagerPreferences.setForceServerRefreshFlag(true);
      super.reload();
      return null;
    });
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
    }).compose(bindUntilEvent(LifecycleEvent.DESTROY)).subscribe(a -> {
      // no-op
    });
  }

  //
  // Store comments methods
  //

  @Override protected RecyclerView.ItemDecoration getItemDecoration() {
    return new HorizontalDividerItemDecoration(getContext(), 0);
  }

  public Observable<Void> createNewCommentFragment(String timelineArticleId) {

    return Observable.just(accountManager.isLoggedIn()).flatMap(isLoggedIn -> {

      if (isLoggedIn) {
        // show fragment CommentDialog
        FragmentManager fm = CommentListFragment.this.getActivity().getSupportFragmentManager();
        CommentDialogFragment commentDialogFragment =
            CommentDialogFragment.newInstanceTimelineArticleComment(timelineArticleId);
        commentDialogFragment.setCommentDialogCallbackContract(this);
        return commentDialogFragment.lifecycle()
            .doOnSubscribe(() -> {
              commentDialogFragment.show(fm, "fragment_comment_dialog");
            })
            .filter(event -> event.equals(FragmentEvent.DESTROY_VIEW))
            .flatMap(event -> Observable.empty());
      }

      return showSignInMessage();
    });
  }

  public Observable<Void> createNewCommentFragment(long storeCommentId, String storeName) {

    return Observable.just(accountManager.isLoggedIn()).flatMap(isLoggedIn -> {

      if (isLoggedIn) {
        // show fragment CommentDialog
        FragmentManager fm = CommentListFragment.this.getActivity().getSupportFragmentManager();
        CommentDialogFragment commentDialogFragment =
            CommentDialogFragment.newInstanceStoreComment(storeCommentId, storeName);
        commentDialogFragment.setCommentDialogCallbackContract(this);

        return commentDialogFragment.lifecycle()
            .doOnSubscribe(() -> {
              commentDialogFragment.show(fm, "fragment_comment_dialog");
            })
            .filter(event -> event.equals(FragmentEvent.DESTROY_VIEW))
            .flatMap(event -> Observable.empty());
      }

      return showSignInMessage();
    });
  }

  @Override public void okSelected(BaseV7Response response, long longAsId, Long previousCommentId,
      String idAsString) {
    if (response instanceof SetComment) {
      ComplexComment complexComment =
          getComplexComment(((SetComment) response).getData().getBody(), previousCommentId,
              ((SetComment) response).getData().getId());

      CommentDisplayable commentDisplayable = new CommentDisplayable(complexComment);

      if (complexComment.getParent() != null) {
        insertChildCommentInsideParent(complexComment);
      } else {
        addDisplayable(0, commentDisplayable, true);
      }
      ManagerPreferences.setForceServerRefreshFlag(true);
      ShowMessage.asSnack(this.getActivity(), R.string.comment_submitted);
    }
  }

  private void insertChildCommentInsideParent(ComplexComment complexComment) {
    displayables.clear();
    boolean added = false;
    ArrayList<Displayable> displayables = new ArrayList<>(comments.size() + 1);
    for (CommentNode commentNode : comments) {
      displayables.add(new CommentDisplayable(new ComplexComment(commentNode,
          createNewCommentFragment(elementIdAsString, commentNode.getComment().getId()))));
      if (commentNode.getComment().getId() == complexComment.getParent().getId() && !added) {
        displayables.add(new CommentDisplayable(complexComment));
        added = true;
      }
    }
    this.displayables = new ArrayList<>(displayables.size());
    this.displayables.add(new DisplayableGroup(displayables));
    clearDisplayables();
    addDisplayables(this.displayables);
  }

  @NonNull
  private ComplexComment getComplexComment(String inputText, Long previousCommentId, long id) {
    Comment comment = new Comment();
    Comment.User user = new Comment.User();
    if (!TextUtils.isEmpty(accountManager.getAccount().getAvatar())) {
      user.setAvatar(accountManager.getAccount().getAvatar());
    } else {
      if (!TextUtils.isEmpty(accountManager.getAccount().getStoreAvatar())) {
        user.setAvatar(accountManager.getAccount().getStoreAvatar());
      }
    }
    user.setName(accountManager.getAccount().getNickname());
    comment.setUser(user);
    comment.setBody(inputText);
    comment.setAdded(new Date());
    comment.setId(id);
    CommentNode commentNode = new CommentNode(comment);
    if (previousCommentId != null) {
      Comment.Parent parent = new Comment.Parent();
      parent.setId(previousCommentId);
      comment.setParent(parent);
      commentNode.setLevel(2);
    }
    if (elementIdAsLong != 0) {
      return new ComplexComment(commentNode,
          createNewCommentFragment(elementIdAsLong, commentNode.getComment().getId(), storeName));
    } else {
      return new ComplexComment(commentNode,
          createNewCommentFragment(elementIdAsString, commentNode.getComment().getId()));
    }
  }
}
