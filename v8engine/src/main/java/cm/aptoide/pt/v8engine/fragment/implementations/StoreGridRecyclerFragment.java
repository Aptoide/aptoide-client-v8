/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
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
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.store.PostCommentForStore;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.util.ThemeUtils;
import cm.aptoide.pt.v8engine.util.Translator;
import cm.aptoide.pt.viewRateAndCommentReviews.CommentDialogWrapper;
import com.jakewharton.rxbinding.view.RxView;
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

  private void reloadComments() {
    // TODO: 5/12/2016 sithengineer
  }

  private Observable<BaseV7Response> submitComment(long storeId, @Nullable Comment comment, String inputText) {
    if(comment!=null) {
      return PostCommentForStore.of(storeId, comment.getId(), inputText,
          AptoideAccountManager.getAccessToken(),
          new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
              DataProvider.getContext()).getAptoideClientUUID()).observe();
    }

    return PostCommentForStore.of(storeId, inputText,
        AptoideAccountManager.getAccessToken(),
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()).getAptoideClientUUID()).observe();
  }

  public Observable<Boolean> showStoreCommentDialogAndSendComment(final long storeId, @Nullable Comment comment) {

    if (AptoideAccountManager.isLoggedIn()) {

      CommentDialogWrapper dialogWrapper = new CommentDialogWrapper();
      dialogWrapper.build(getContext(), " ?? "); // setup title

      dialogWrapper.onCommentButton()
          .filter(inputText -> {
            AptoideUtils.SystemU.hideKeyboard(StoreGridRecyclerFragment.this.getActivity());
            if (TextUtils.isEmpty(inputText)) {
              dialogWrapper.enableError(AptoideUtils.StringU.getResString(R.string.error_MARG_107));
              return false;
            }
            dialogWrapper.disableError();
            return true;
          })
          .flatMap(inputText -> submitComment(storeId, comment, inputText))
          .compose(bindUntilEvent(LifecycleEvent.DESTROY_VIEW))
          .subscribe(wsResponse -> {
            if (wsResponse.isOk()) {
              ManagerPreferences.setForceServerRefreshFlag(true);
              reloadComments();
              Logger.d(TAG, "comment to review added");
              ShowMessage.asSnack(StoreGridRecyclerFragment.this,
                  R.string.comment_submitted);
              dialogWrapper.dismiss();
              return;
            }

            ShowMessage.asSnack(StoreGridRecyclerFragment.this, R.string.error_occured);
          }, e -> {
            Logger.e(TAG, e);
            ShowMessage.asSnack(StoreGridRecyclerFragment.this, R.string.error_occured);
          });

    } else {
      return ShowMessage.asObservableSnack(this.getActivity(), R.string.you_need_to_be_logged_in, R.string.login,
          snackView -> {
            AptoideAccountManager.openAccountManager(StoreGridRecyclerFragment.this.getContext());
          }).map(a -> false);
    }

    return Observable.fromCallable(() -> true);
  }

  // special case. this would need a refactoring to be done properly
  @Override void caseListStoreComments(String url,
      BaseRequestWithStore.StoreCredentials storeCredentials, boolean refresh) {
    super.caseListStoreComments(url, storeCredentials, refresh);
    floatingActionButton.setVisibility(View.VISIBLE);
    RxView.clicks(floatingActionButton)
        .compose(bindUntilEvent(LifecycleEvent.DESTROY_VIEW))
        .flatMap(a -> showStoreCommentDialogAndSendComment(storeCredentials.getId(), null))
        .subscribe(b -> {
          /* nothing to do here*/
        }, err -> {
          CrashReports.logException(err);
        });
  }
}
