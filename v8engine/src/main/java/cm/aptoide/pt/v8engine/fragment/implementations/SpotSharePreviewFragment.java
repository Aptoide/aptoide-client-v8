package cm.aptoide.pt.v8engine.fragment.implementations;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import cm.aptoide.pt.spotandshareandroid.HighwayActivity;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.FragmentView;
import cm.aptoide.pt.v8engine.presenter.SpotSharePreviewPresenter;
import cm.aptoide.pt.v8engine.view.SpotSharePreviewView;
import com.jakewharton.rxbinding.view.RxView;
import rx.Observable;

/**
 * Created by marcelobenites on 23/02/17.
 */
public class SpotSharePreviewFragment extends FragmentView implements SpotSharePreviewView {

  private static String SHOW_TOOLBAR_KEY = "SHOW_TOOLBAR_KEY";
  private Button startButton;
  private Toolbar toolbar;
  private boolean showToolbar;

  public static Fragment newInstance(boolean showToolbar) {
    Bundle args = new Bundle();
    args.putBoolean(SHOW_TOOLBAR_KEY, showToolbar);
    Fragment fragment = new SpotSharePreviewFragment();
    Bundle arguments = fragment.getArguments();
    if (arguments != null) {
      args.putAll(arguments);
    }
    fragment.setArguments(args);

    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    showToolbar = getArguments().getBoolean(SHOW_TOOLBAR_KEY);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    startButton = (Button) view.findViewById(R.id.fragment_spot_share_preview_start_button);
    toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    attachPresenter(
        new SpotSharePreviewPresenter(this, showToolbar, getString(R.string.spot_share)),
        savedInstanceState);
  }

  private Toolbar setupToolbar(String title) {
    setHasOptionsMenu(true);

    toolbar.setLogo(R.drawable.logo_toolbar);

    toolbar.setTitle(title);

    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);

    return toolbar;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_spot_share_preview, container, false);
  }

  @Override public Observable<Void> startSelection() {
    return RxView.clicks(startButton);
  }

  @Override public void navigateToSpotShareView() {
    startActivity(new Intent(getContext(), HighwayActivity.class));
  }

  @Override public void showToolbar(String title) {
    setupToolbar(title);
    toolbar.setVisibility(View.VISIBLE);
  }

  @Override public void finish() {
    getFragmentManager().popBackStack();
  }
}
