package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragment;
import rx.Observable;

public class CommentListFragment extends GridRecyclerFragment {

  private static final String TAG = StoreGridRecyclerFragment.class.getName();

  private static final String COMMENT_TYPE = "comment_type";
  private static final String ELEMENT_ID = "element_id";

  private CommentType commentType;
  private String elementId;

  private FloatingActionButton floatingActionButton;

  public static Fragment newInstance(CommentType commentType, String elementId) {
    Bundle args = new Bundle();
    args.putString(ELEMENT_ID, elementId);
    args.putString(COMMENT_TYPE, commentType.name());

    CommentListFragment fragment = new CommentListFragment();
    fragment.setArguments(args);
    return fragment;
  }

  //@Override public void setupToolbar() {
  //  // It's not calling super cause it does nothing in the middle class}
  //  // StoreTabGridRecyclerFragment.
  //  if (toolbar != null) {
  //    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
  //    ((AppCompatActivity) getActivity()).getSupportActionBar()
  //        .setTitle(Translator.translate(title));
  //    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  //    toolbar.setLogo(R.drawable.ic_aptoide_toolbar);
  //  }
  //}

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
}
