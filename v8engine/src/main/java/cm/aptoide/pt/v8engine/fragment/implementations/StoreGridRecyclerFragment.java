/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.util.ThemeUtils;
import cm.aptoide.pt.v8engine.util.Translator;
import cm.aptoide.pt.viewRateAndCommentReviews.CommentDialogFragment;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.FragmentEvent;
import rx.Observable;

/**
 * Created by neuro on 10-05-2016.
 */
public class StoreGridRecyclerFragment extends StoreTabGridRecyclerFragment {

  private static final String TAG = StoreGridRecyclerFragment.class.getName();

  private FloatingActionButton floatingActionButton;

  public static StoreGridRecyclerFragment newInstance(Event event, String title, String storeTheme,
      String tag) {
    Bundle args = buildBundle(event, title, storeTheme, tag);
    StoreGridRecyclerFragment fragment = new StoreGridRecyclerFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public static StoreGridRecyclerFragment newInstance(Event event, String title) {
    Bundle args = buildBundle(event, title);

    StoreGridRecyclerFragment fragment = new StoreGridRecyclerFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void setupToolbar() {
    // It's not calling super cause it does nothing in the middle class}
    // StoreTabGridRecyclerFragment.
    if (toolbar != null) {
      ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
      ((AppCompatActivity) getActivity()).getSupportActionBar()
          .setTitle(Translator.translate(title));
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
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fabAdd);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    if (storeTheme != null) {
      ThemeUtils.setStoreTheme(getActivity(), storeTheme);
      ThemeUtils.setStatusBarThemeColor(getActivity(), StoreThemeEnum.get(storeTheme));
    }
    return super.onCreateView(inflater, container, savedInstanceState);
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
          AptoideAccountManager.openAccountManager(StoreGridRecyclerFragment.this.getContext());
        }).flatMap(a -> Observable.empty());
  }

  // Used method for each single reply in the list view and the new comment button
  @Override public Observable<Void> showStoreCommentFragment(final long storeId,
      @NonNull Comment comment, String storeName) {

    return Observable.just(AptoideAccountManager.isLoggedIn()).flatMap(isLoggedIn -> {

      if (isLoggedIn) {
        // show fragment CommentDialog
        FragmentManager fm = StoreGridRecyclerFragment.this.getActivity().getFragmentManager();
        CommentDialogFragment commentDialogFragment =
            CommentDialogFragment.newInstanceStoreCommentReply(storeId, comment.getId(), storeName);

        return commentDialogFragment.lifecycle()
            .doOnSubscribe(() -> commentDialogFragment.show(fm, "fragment_comment_dialog"))
            .filter(event -> event.equals(FragmentEvent.DESTROY_VIEW))
            .flatMap(event -> reloadComments());
      }

      return showSignInMessage();
    });
  }

  // special case. this would need a refactoring to be done properly
  @Override void caseListStoreComments(String url,
      BaseRequestWithStore.StoreCredentials storeCredentials, boolean refresh) {
    super.caseListStoreComments(url, storeCredentials, refresh);
    floatingActionButton.setVisibility(View.VISIBLE);

    final long storeId =
        (storeCredentials != null && storeCredentials.getId() != null) ? storeCredentials.getId()
            : 0;

    final String storeName =
        (storeCredentials != null && !TextUtils.isEmpty(storeCredentials.getName()))
            ? storeCredentials.getName() : " ";

    RxView.clicks(floatingActionButton)
        .compose(bindUntilEvent(LifecycleEvent.DESTROY_VIEW))
        .subscribe(a -> {
          if (AptoideAccountManager.isLoggedIn()) {
            FragmentManager fm = StoreGridRecyclerFragment.this.getActivity().getFragmentManager();
            CommentDialogFragment commentDialogFragment =
                CommentDialogFragment.newInstanceStoreComment(storeId, storeName);

            // check if fragment is already visible.
            if (fm.findFragmentByTag("fragment_comment_dialog_list") != null) return;

            commentDialogFragment.show(fm, "fragment_comment_dialog_list");
            commentDialogFragment.lifecycle()
                .filter(event -> event.equals(FragmentEvent.DESTROY_VIEW))
                .compose(bindUntilEvent(LifecycleEvent.DESTROY_VIEW))
                .flatMap(event -> reloadComments())
                .subscribe(b -> {
                  // does nothing. further optimization needed in this observable
                }, err -> {
                  Logger.e(TAG, err);
                  CrashReports.logException(err);
                });
          } else {
            ShowMessage.asSnack(StoreGridRecyclerFragment.this.getActivity(),
                R.string.you_need_to_be_logged_in, R.string.login, snackView -> {
                  AptoideAccountManager.openAccountManager(snackView.getContext());
                });
          }
        }, e -> {
          Logger.e(TAG, e);
          ShowMessage.asSnack(StoreGridRecyclerFragment.this, R.string.error_occured);
        });
  }
}
