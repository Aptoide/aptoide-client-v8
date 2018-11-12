package cm.aptoide.pt.comments.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.comment.CommentMapper;
import cm.aptoide.pt.comment.CommentsAdapter;
import cm.aptoide.pt.comment.CommentsListManager;
import cm.aptoide.pt.comment.CommentsNavigator;
import cm.aptoide.pt.comment.SubmitComment;
import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.comment.data.User;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.store.view.StoreLatestCommentsDisplayable;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.recycler.widget.Widget;
import java.util.ArrayList;
import java.util.Date;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorNotImplementedException;
import rx.subjects.PublishSubject;

public class StoreLatestCommentsWidget extends Widget<StoreLatestCommentsDisplayable> {

  private RecyclerView recyclerView;

  private long storeId;
  private String storeName;
  private AptoideAccountManager accountManager;
  private AccountNavigator accountNavigator;
  private BodyInterceptor<BaseBody> baseBodyInterceptor;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private TokenInvalidator tokenInvalidator;
  private PublishSubject<Comment> postComment;
  private PublishSubject<Comment> commentClickEvent;
  private PublishSubject<Long> userClickEvent;
  private CommentMapper commentMapper;
  private CommentsAdapter commentsAdapter;
  private CommentsNavigator commentsNavigator;
  private CommentsListManager commentsListManager;

  public StoreLatestCommentsWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    recyclerView = (RecyclerView) itemView.findViewById(R.id.comments);
    postComment = PublishSubject.create();
    commentClickEvent = PublishSubject.create();
    userClickEvent = PublishSubject.create();
  }

  @Override public void bindView(StoreLatestCommentsDisplayable displayable) {
    accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();
    tokenInvalidator =
        ((AptoideApplication) getContext().getApplicationContext()).getTokenInvalidator();
    baseBodyInterceptor =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountSettingsBodyInterceptorPoolV7();
    accountNavigator = ((ActivityResultNavigator) getContext()).getAccountNavigator();
    httpClient = ((AptoideApplication) getContext().getApplicationContext()).getDefaultClient();
    converterFactory = WebService.getDefaultConverter();

    LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
    recyclerView.setLayoutManager(layoutManager);
    commentsAdapter =
        new CommentsAdapter(new ArrayList<>(), AptoideUtils.DateTimeU.getInstance(getContext()),
            commentClickEvent, R.layout.comment_item, postComment, userClickEvent);
    storeId = displayable.getStoreId();
    storeName = displayable.getStoreName();
    commentMapper = displayable.getCommentMapper();
    commentsListManager = displayable.getCommentsListManager();
    commentsNavigator = displayable.getCommentsNavigator();

    // TODO: 9/12/2016 create load and store methods when fragment is destroyed

    setAdapter(displayable);
    handleClicksOnPost();
    handleClickOnComment();
    handleClickOnUser();
  }

  private void handleClicksOnPost() {
    compositeSubscription.add(postComment.doOnNext(__ -> hideKeyboard())
        .flatMap(comment -> accountManager.accountStatus()
            .map(account -> {
              if (account.isLoggedIn()) {
                return account;
              } else {
                return null;
              }
            })
            .filter(account -> account != null)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapCompletable(account -> {
              addLocalComment(comment, account);
              return commentsListManager.postComment(comment, storeId);
            }))
        .subscribe(comment -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        }));
  }

  private void handleClickOnComment() {
    compositeSubscription.add(commentClickEvent.doOnNext(
        comment -> commentsNavigator.navigateToCommentView(comment, comment.getId()))
        .subscribe(comment -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        }));
  }

  private void handleClickOnUser() {
    compositeSubscription.add(userClickEvent.doOnNext(id -> commentsNavigator.navigateToStore(id))
        .subscribe(comment -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        }));
  }

  private void hideKeyboard() {
    View view = getActivityNavigator().getActivity()
        .getCurrentFocus();
    if (view != null) {
      InputMethodManager imm = (InputMethodManager) getActivityNavigator().getActivity()
          .getSystemService(Context.INPUT_METHOD_SERVICE);
      assert imm != null;
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }

  private void addLocalComment(Comment comment, Account account) {
    commentsAdapter.addSingleComment(new Comment(comment.getId(), comment.getMessage(),
        new User(-1, account.getAvatar(), account.getNickname()), 0, new Date()));
  }

  private void setAdapter(StoreLatestCommentsDisplayable displayable) {
    compositeSubscription.add(accountManager.accountStatus()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(account -> {
          String avatar;
          if (account.isLoggedIn()) {
            avatar = account.getAvatar();
          } else {
            avatar = null;
          }
          recyclerView.setAdapter(commentsAdapter);
          commentsAdapter.setComments(commentMapper.map(displayable.getComments()),
              new SubmitComment(avatar));
        })
        .subscribe(comment -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        }));
    recyclerView.setAdapter(commentsAdapter);

    //recyclerView.setAdapter(new CommentListAdapter(storeId, storeName, comments,
    //    getContext().getSupportFragmentManager(), recyclerView,
    //    Observable.fromCallable(() -> reloadComments()), accountManager, accountNavigator,
    //    getFragmentNavigator(),
    //    ((AptoideApplication) getContext().getApplicationContext()).getFragmentProvider()));
  }

  //private Void reloadComments() {
  //  ManagerPreferences.setForceServerRefreshFlag(true,
  //      ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences());
  //  compositeSubscription.add(
  //      ListCommentsRequest.of(storeId, 0, 3, false, baseBodyInterceptor, httpClient,
  //          converterFactory, tokenInvalidator,
  //          ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences())
  //          .observe()
  //          .subscribeOn(Schedulers.io())
  //          .observeOn(AndroidSchedulers.mainThread())
  //          .subscribe(listComments -> {
  //            commentsAdapter.addComments(commentMapper.map(listComments.getDataList()
  //                .getList()));
  //          }, err -> {
  //            CrashReport.getInstance()
  //                .log(err);
  //          }));
  //  return null;
  //}

  //private static class CommentListAdapter extends BaseAdapter {
  //
  //  private final AptoideAccountManager accountManager;
  //  private AccountNavigator accountNavigator;
  //
  //  CommentListAdapter(long storeId, @NonNull String storeName, @NonNull List<Comment> comments,
  //      @NonNull FragmentManager fragmentManager, @NonNull View view,
  //      Observable<Void> reloadComments, AptoideAccountManager accountManager,
  //      AccountNavigator accountNavigator, FragmentNavigator fragmentNavigator,
  //      FragmentProvider fragmentProvider) {
  //    this.accountManager = accountManager;
  //    this.accountNavigator = accountNavigator;
  //
  //    final CommentOperations commentOperations = new CommentOperations();
  //    List<CommentNode> sortedComments =
  //        commentOperations.flattenByDepth(commentOperations.transform(comments));
  //
  //    ArrayList<Displayable> displayables = new ArrayList<>(sortedComments.size());
  //    for (CommentNode commentNode : sortedComments) {
  //      displayables.add(new StoreCommentDisplayable(new ComplexComment(commentNode,
  //          showStoreCommentFragment(storeId, commentNode.getComment(), storeName, fragmentManager,
  //              view, reloadComments)), fragmentNavigator, fragmentProvider));
  //    }
  //    addDisplayables(displayables);
  //  }
  //
  //  private Completable showStoreCommentFragment(final long storeId, @NonNull final Comment comment,
  //      @NonNull final String storeName, @NonNull final FragmentManager fragmentManager,
  //      @NonNull final View view, Observable<Void> reloadComments) {
  //
  //    return accountManager.accountStatus()
  //        .first()
  //        .toSingle()
  //        .flatMapCompletable(account -> {
  //          if (account.isLoggedIn()) {
  //            // show fragment CommentDialog
  //            CommentDialogFragment commentDialogFragment =
  //                CommentDialogFragment.newInstanceStoreCommentReply(storeId, comment.getId(),
  //                    storeName);
  //
  //            return commentDialogFragment.lifecycle()
  //                .doOnSubscribe(() -> commentDialogFragment.show(fragmentManager,
  //                    "fragment_comment_dialog_latest"))
  //                .filter(event -> event.equals(FragmentEvent.DESTROY_VIEW))
  //                .flatMap(event -> reloadComments)
  //                .toCompletable();
  //          }
  //
  //          return showSignInMessage(view);
  //        });
  //  }
  //
  //  private Completable showSignInMessage(@NonNull final View view) {
  //    // R.string.you_need_to_be_logged_in, R.string.login,
  //    return Completable.fromAction(() -> {
  //      Snackbar.make(view, R.string.you_need_to_be_logged_in, Snackbar.LENGTH_LONG)
  //          .setAction(R.string.login, snackView -> accountNavigator.navigateToAccountView(
  //              AccountAnalytics.AccountOrigins.LATEST_COMMENTS_STORE))
  //          .show();
  //    });
  //  }
  //}
}
