package cm.aptoide.pt.v8engine.fragment.implementations;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import cm.aptoide.pt.shareappsandroid.HighwayActivity;
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

  private Button startButton;

  public static Fragment newInstance() {
    return new SpotSharePreviewFragment();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_spot_share_preview, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    startButton = (Button) view.findViewById(R.id.fragment_spot_share_preview_start_button);
    attachPresenter(new SpotSharePreviewPresenter(this), savedInstanceState);
  }

  @Override public Observable<Void> startSelection() {
    return RxView.clicks(startButton);
  }

  @Override public void navigateToSpotShareView() {
    startActivity(new Intent(getContext(), HighwayActivity.class));
  }
}
