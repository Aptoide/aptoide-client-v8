package cm.aptoide.pt.spotandshareapp.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.fragment.FragmentView;

/**
 * Created by filipe on 12-09-2017.
 */

public class ShareAptoideFragment extends FragmentView implements ShareAptoideView {

  private Toolbar toolbar;
  private LinearLayout shareAptoideLinearLayout;
  private TextView shareAptoideFirstInstruction;
  private TextView shareAptoideLink;

  public static Fragment newInstance() {
    Fragment fragment = new ShareAptoideFragment();
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_spotandshare_share_aptoide, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    toolbar = (Toolbar) view.findViewById(R.id.spotandshare_toolbar);
    shareAptoideLinearLayout = (LinearLayout) view.findViewById(R.id.share_aptoide_layout);
    shareAptoideFirstInstruction =
        (TextView) view.findViewById(R.id.share_aptoide_first_instruction);
    shareAptoideLink = (TextView) view.findViewById(R.id.share_aptoide_link);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
  }

  @Override public void onDestroy() {
    super.onDestroy();
  }
}
