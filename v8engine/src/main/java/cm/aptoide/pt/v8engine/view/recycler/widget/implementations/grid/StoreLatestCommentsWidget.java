package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.ListCommentsRequest;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.navigation.AccountNavigator;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.BaseBodyInterceptor;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.preferences.AdultContent;
import cm.aptoide.pt.v8engine.preferences.Preferences;
import cm.aptoide.pt.v8engine.preferences.SecurePreferences;
import cm.aptoide.pt.v8engine.util.CommentOperations;
import cm.aptoide.pt.v8engine.view.custom.HorizontalDividerItemDecoration;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CommentDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.StoreLatestCommentsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import cm.aptoide.pt.viewRateAndCommentReviews.CommentDialogFragment;
import cm.aptoide.pt.viewRateAndCommentReviews.CommentNode;
import cm.aptoide.pt.viewRateAndCommentReviews.ComplexComment;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class StoreLatestCommentsWidget extends Widget<StoreLatestCommentsDisplayable> {

  private RecyclerView recyclerView;

  private long storeId;
  private String storeName;
  private AptoideAccountManager accountManager;
  private AccountNavigator accountNavigator;
  private BodyInterceptor<BaseBody> baseBodyInterceptor;

  public StoreLatestCommentsWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    recyclerView = (RecyclerView) itemView.findViewById(R.id.comments);
  }

  @Override public void bindView(StoreLatestCommentsDisplayable displayable) {
    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    baseBodyInterceptor = ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptor();
    accountNavigator =
        new AccountNavigator(getContext(), getNavigationManager(), accountManager);
    LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.addItemDecoration(new HorizontalDividerItemDecoration(getContext()));

    storeId = displayable.getStoreId();
    storeName = displayable.getStoreName();

    // TODO: 9/12/2016 sithengineer create load and store methods when fragment is destroyed

    setAdapter(displayable.getComments());
  }

  private void setAdapter(List<Comment> comments) {
    recyclerView.setAdapter(new CommentListAdapter(storeId, storeName, comments,
        getContext().getSupportFragmentManager(), recyclerView,
        Observable.fromCallable(() -> reloadComments()), accountManager, accountNavigator));
  }

  private Void reloadComments() {
    ManagerPreferences.setForceServerRefreshFlag(true);
    compositeSubscription.add(ListCommentsRequest.of(storeId, 0, 3,
        false, baseBodyInterceptor)
        .observe()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(listComments -> {
          setAdapter(listComments.getDatalist().getList());
        }, err -> {
          CrashReport.getInstance().log(err);
        }));
    return null;
  }

  private static class CommentListAdapter extends BaseAdapter {

    private final AptoideAccountManager accountManager;
    private AccountNavigator accountNavigator;

    CommentListAdapter(long storeId, @NonNull String storeName, @NonNull List<Comment> comments,
        @NonNull FragmentManager fragmentManager, @NonNull View view,
        Observable<Void> reloadComments, AptoideAccountManager accountManager,
        AccountNavigator accountNavigator) {
      this.accountManager = accountManager;
      this.accountNavigator = accountNavigator;

      final CommentOperations commentOperations = new CommentOperations();
      List<CommentNode> sortedComments =
          commentOperations.flattenByDepth(commentOperations.transform(comments));

      ArrayList<Displayable> displayables = new ArrayList<>(sortedComments.size());
      for (CommentNode commentNode : sortedComments) {
        displayables.add(new CommentDisplayable(new ComplexComment(commentNode,
            showStoreCommentFragment(storeId, commentNode.getComment(), storeName, fragmentManager,
                view, reloadComments))));
      }
      addDisplayables(displayables);
    }

    private Observable<Void> showStoreCommentFragment(final long storeId,
        @NonNull final Comment comment, @NonNull final String storeName,
        @NonNull final FragmentManager fragmentManager, @NonNull final View view,
        Observable<Void> reloadComments) {

      return Observable.just(accountManager.isLoggedIn()).flatMap(isLoggedIn -> {

        if (isLoggedIn) {
          // show fragment CommentDialog
          CommentDialogFragment commentDialogFragment =
              CommentDialogFragment.newInstanceStoreCommentReply(storeId, comment.getId(),
                  storeName);

          return commentDialogFragment.lifecycle()
              .doOnSubscribe(() -> commentDialogFragment.show(fragmentManager,
                  "fragment_comment_dialog_latest"))
              .filter(event -> event.equals(FragmentEvent.DESTROY_VIEW))
              .flatMap(event -> reloadComments);
        }

        return showSignInMessage(view);
      });
    }

    private Observable<Void> showSignInMessage(@NonNull final View view) {
      return ShowMessage.asObservableSnack(view, R.string.you_need_to_be_logged_in, R.string.login,
          snackView -> {
            accountNavigator.navigateToAccountView();
          }).flatMap(a -> Observable.empty());
    }
  }
}
